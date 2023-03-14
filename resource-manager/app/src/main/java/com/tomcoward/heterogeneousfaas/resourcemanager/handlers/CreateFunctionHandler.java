package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.WorkerException;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.LearningManager;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionExecutionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class CreateFunctionHandler implements com.sun.net.httpserver.HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static int MAX_TRAINING_EXECUTIONS = 1000;

    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;
    private final LearningManager learningManager;
    private final InvokeFunctionHandler invokeFunctionHandler;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, IFunctionExecutionRepository functionExecutionsRepo, AWSLambda awsLambda, Kubernetes kubernetes, LearningManager learningManager) {
        this.functionsRepo = functionsRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;
        this.learningManager = learningManager;

        this.invokeFunctionHandler = new InvokeFunctionHandler(functionsRepo, functionExecutionsRepo, awsLambda, kubernetes, learningManager);
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject functionObject = HttpHelper.getRequestBody(exchange, "function");

            Function function = createFunction(functionObject);

            LOGGER.log(Level.INFO, String.format("CreateFunctionHandler function created: \"%s\"", function.getName()));

            String response = gson.toJson(function);
            HttpHelper.sendResponse(exchange, 200, response);
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
            String response = "There was an issue creating your function";
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private Function createFunction(JsonObject functionObject) throws DBClientException, IntegrationException, IOException, WorkerException {
        Function function = new Function(functionObject);

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
        JsonArray exampleInputs = functionObject.getJsonArray("example_inputs");
        runTraining(function, exampleInputs);

        return function;
    }

    private void runTraining(Function function, JsonArray exampleInputs) throws WorkerException, IntegrationException, DBClientException {
        // for AWS (if cloud supported)
        if (function.isCloudSupported()) {
            LOGGER.log(Level.INFO, String.format("Running training on AWS (cloud) worker for function: %s", function.getName()));

            ArrayList<String> functionPayloadArray = new ArrayList<>();
            for (int i = 0; i < Math.min(exampleInputs.toArray().length, MAX_TRAINING_EXECUTIONS); i++) {
                // add to function payload (which gets incrementally larger)
                functionPayloadArray.add(exampleInputs.get(i).toString());

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(AWSLambda.WORKER_NAME, function, functionPayload, 0);

                invokeFunctionHandler.recordFunctionExecution(function.getName(), AWSLambda.WORKER_NAME, functionPayloadArray.size(), response);
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

                invokeFunctionHandler.recordFunctionExecution(function.getName(), Kubernetes.WORKER_NAME, functionPayloadArray.size(), response);
                LOGGER.log(Level.INFO, String.format("%d executions completed...", i+1));
            }

            LOGGER.log(Level.INFO, "Kubernetes (edge) training complete");
        }

        // trigger training by Learning Manager
        LOGGER.log(Level.INFO, String.format("Triggering ML training for function: %s", function.getName()));
        learningManager.triggerTraining(function.getName());
        LOGGER.log(Level.INFO, "ML training complete");
    }
}
