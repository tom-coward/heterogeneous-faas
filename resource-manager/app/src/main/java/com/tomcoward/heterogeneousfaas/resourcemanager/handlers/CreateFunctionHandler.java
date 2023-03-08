package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.WorkerException;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionExecutionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IWorkerRepository;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class CreateFunctionHandler implements com.sun.net.httpserver.HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final IWorkerRepository workersRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;
    private final InvokeFunctionHandler invokeFunctionHandler;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, IWorkerRepository workersRepo, IFunctionExecutionRepository functionExecutionsRepo, AWSLambda awsLambda, Kubernetes kubernetes) {
        this.functionsRepo = functionsRepo;
        this.workersRepo = workersRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;

        this.invokeFunctionHandler = new InvokeFunctionHandler(functionsRepo, workersRepo, functionExecutionsRepo, awsLambda, kubernetes);
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

        // if aws supported, add to AWS Lambda
        if (function.isCloudAWSSupported()) {
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
        // iterate through each worker to build model for each
        List<Worker> workers = workersRepo.getAll();

        if (workers.size() == 0) {
            throw new WorkerException("No workers available to train function on");
        }

        ArrayList<String> functionPayloadArray = new ArrayList<>();

        for (Worker worker : workers) {
            for (int i=0; i < Math.min(exampleInputs.toArray().length, 100); i++) {
                // add to function payload (which gets incrementally larger)
                functionPayloadArray.add(exampleInputs.get(i).toString());

                String functionPayload = functionPayloadArray.toString();

                InvokeFunctionHandler.FunctionInvocationResponse response = invokeFunctionHandler.invokeWorker(worker, function, functionPayload);

                System.out.println(response.getResponse());

                invokeFunctionHandler.recordFunctionExecution(function.getName(), worker.getId(), functionPayloadArray.size(), response);
            }
        }
    }
}
