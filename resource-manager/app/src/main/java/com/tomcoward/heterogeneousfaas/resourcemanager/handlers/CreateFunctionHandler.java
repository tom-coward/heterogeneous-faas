package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionExecutionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class CreateFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static int MAX_TRAINING_EXECUTIONS = 1000;

    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;
    private final LearningManager learningManager;
    private final Docker docker;
    private final InvokeFunctionHandler invokeFunctionHandler;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, IFunctionExecutionRepository functionExecutionsRepo, AWSLambda awsLambda, Kubernetes kubernetes, LearningManager learningManager, Docker docker) {
        this.functionsRepo = functionsRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;
        this.learningManager = learningManager;
        this.docker = docker;

        this.invokeFunctionHandler = new InvokeFunctionHandler(functionsRepo, functionExecutionsRepo, awsLambda, kubernetes, learningManager);
    }


    public void handleRequest(HttpServerExchange exchange) {
        // handle request asynchronously in new thread if currently blocking IO thread
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        try {
            JsonObject functionObject = HttpHelper.getRequestBody(exchange, "function");
            JsonArray exampleInputs = functionObject.getJsonArray("example_inputs");

            Function function = new Function(functionObject);

            // build and push function as Docker image (to AWS ECR)
            function = docker.buildAndPushImage(function);

            // create function on cloud and edge workers
            function = createFunction(function, exampleInputs);

            LOGGER.log(Level.INFO, String.format("CreateFunctionHandler function created: \"%s\"", function.getName()));

            String response = gson.toJson(function);
            HttpHelper.sendResponse(exchange, 200, response);

            // once function created, trigger Learning Manager run function clustering asynchronously
            try {
                learningManager.runFunctionClustering();
            } catch (FunctionClusteringException ex) {
                LOGGER.log(Level.SEVERE, "Error running function clustering", ex);
            }
        } catch (DBClientException ex) {
            // return error to client
            String response = "There was an issue saving your function";
            HttpHelper.sendResponse(exchange, 500, response);
        } catch (IOException ex) {
            // return error to client
            LOGGER.log(Level.SEVERE, "Error creating function", ex);
            String response = "The function object was invalid";
            HttpHelper.sendResponse(exchange, 400, response);
        } catch (Exception ex) {
            // return error to client
            LOGGER.log(Level.SEVERE, "Error creating function", ex);
            String response = String.format("There was an issue creating your function: %s", ex.getMessage());
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private Function createFunction(Function function, JsonArray exampleInputs) throws DBClientException, IntegrationException, IOException, WorkerException {
        LOGGER.log(Level.INFO, String.format("Creating function: %s", function.getName()));

        // if aws supported, add to AWS Lambda
        if (function.isCloudSupported()) {
            function = awsLambda.createFunction(function);
        }

        // if edge supported, add to Kubernetes
        if (function.isEdgeSupported()) {
            function = kubernetes.createFunction(function);
        }

        // save function in database
        functionsRepo.create(function);

        // run training on function
        runTraining(function, exampleInputs);

        return function;
    }

    private void runTraining(Function function, JsonArray exampleInputs) throws WorkerException, IntegrationException, DBClientException {
        // collect minimal training data initially
        if (function.isCloudSupported()) {
            LOGGER.log(Level.INFO, String.format("Running minimal training on AWS (cloud) worker for function: %s", function.getName()));

            // collect training data for every 100n input size
            ArrayList<String> functionPayloadArray = new ArrayList<>();
            for (int i = 0; i < Math.min(exampleInputs.toArray().length - 100, MAX_TRAINING_EXECUTIONS - 100); i += 100) {
                // add to function payload (which gets incrementally larger)
                for (int x = i; x < Math.min(i+100, exampleInputs.toArray().length); x++) {
                    functionPayloadArray.add(exampleInputs.get(x).toString());
                }

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(AWSLambda.WORKER_NAME, function, functionPayload, 0);

                invokeFunctionHandler.recordFunctionExecution(function.getName(), AWSLambda.WORKER_NAME, functionPayloadArray.size(), response, true);
                LOGGER.log(Level.INFO, String.format("%d executions completed...", (i+1)/100));
            }

            LOGGER.log(Level.INFO, "AWS (cloud) minimal training complete");
        }

        if (function.isEdgeSupported()) {
            LOGGER.log(Level.INFO, String.format("Running minimal training on Kubernetes (edge) worker for function: %s", function.getName()));

            // collect training data for every 100n input size
            ArrayList<String> functionPayloadArray = new ArrayList<>();
            for (int i = 0; i < Math.min(exampleInputs.toArray().length - 100, MAX_TRAINING_EXECUTIONS - 100); i += 100) {
                // add to function payload (which gets incrementally larger)
                for (int x = i; x < Math.min(i+100, exampleInputs.toArray().length); x++) {
                    functionPayloadArray.add(exampleInputs.get(x).toString());
                }

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(Kubernetes.WORKER_NAME, function, functionPayload, 0);

                invokeFunctionHandler.recordFunctionExecution(function.getName(), AWSLambda.WORKER_NAME, functionPayloadArray.size(), response, true);
                LOGGER.log(Level.INFO, String.format("%d executions completed...", (i+1)/100));
            }

            LOGGER.log(Level.INFO, "Kubernetes (edge) minimal training complete");
        }

        // attempt transfer learning first via. Learning Manager
        try {
            learningManager.transferLearn(function.getName());
            LOGGER.log(Level.INFO, "ML training (transfer learning) complete");
            return;
        } catch (TransferLearningException ex) {
            LOGGER.log(Level.INFO, "Transfer learning failed - proceeding to training with new training data");
            // if transfer learning fails, proceed to standard training data collection flow...
        }

        // for AWS (if cloud supported)
        if (function.isCloudSupported()) {
            LOGGER.log(Level.INFO, String.format("Running training on AWS (cloud) worker for function: %s", function.getName()));

            ArrayList<String> functionPayloadArray = new ArrayList<>();
            for (int i = 0; i < Math.min(exampleInputs.toArray().length, MAX_TRAINING_EXECUTIONS); i++) {
                // add to function payload (which gets incrementally larger)
                functionPayloadArray.add(exampleInputs.get(i).toString());

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(AWSLambda.WORKER_NAME, function, functionPayload, 0);

                invokeFunctionHandler.recordFunctionExecution(function.getName(), AWSLambda.WORKER_NAME, functionPayloadArray.size(), response, true);
                LOGGER.log(Level.INFO, String.format("%d executions completed...", i+1));
            }

            LOGGER.log(Level.INFO, "AWS (cloud) training complete");
        }

        // for Kubernetes (if edge supported)
        if (function.isEdgeSupported()) {
            LOGGER.log(Level.INFO, String.format("Running training on Kubernetes (edge) worker for function: %s", function.getName()));

            ArrayList<String> functionPayloadArray = new ArrayList<>();
            for (int i = 0; i < Math.min(exampleInputs.toArray().length, MAX_TRAINING_EXECUTIONS); i++) {
                // add to function payload (which gets incrementally larger)
                functionPayloadArray.add(exampleInputs.get(i).toString());

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(Kubernetes.WORKER_NAME, function, functionPayload, 0);

                invokeFunctionHandler.recordFunctionExecution(function.getName(), Kubernetes.WORKER_NAME, functionPayloadArray.size(), response, true);
                LOGGER.log(Level.INFO, String.format("%d executions completed...", i+1));
            }

            LOGGER.log(Level.INFO, "Kubernetes (edge) training complete");
        }

        // trigger model training by Learning Manager
        LOGGER.log(Level.INFO, String.format("Triggering ML training for function: %s", function.getName()));
        learningManager.triggerTraining(function.getName());
        LOGGER.log(Level.INFO, "ML training complete");
    }
}
