package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

public class InvokeFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;

    public InvokeFunctionHandler(IFunctionRepository functionsRepo, AWSLambda awsLambda, Kubernetes kubernetes) {
        this.functionsRepo = functionsRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject requestBody = HttpHelper.getRequestBody(exchange, null);

            // get name of function to be invoked
            String functionName = requestBody.getString("function_name");
            if (functionName == null || functionName.trim().isEmpty()) {
                throw new FunctionException(String.format("No function with the name %s exists to be invoked", functionName));
            }

            // get payload of function (if any)
            JsonObject functionPayload = requestBody.getJsonObject("function_payload");

            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload.toString()));

            Function function = functionsRepo.get(functionName);

            // invoke in AWS
            Worker worker = new Worker(Worker.Host.CLOUD_AWS, Worker.Status.AVAILABLE);

            // invoke in Kubernetes
            //Worker k8sWorker = new Worker(Worker.Host.EDGE_KUBERNETES, Worker.Status.AVAILABLE);
            //String knativeResponse = invokeWorker(k8sWorker, function, functionPayload);

            // TODO: call ML Manager to choose worker

            // returns list of worker types
//            ArrayList<Worker> workers = new ArrayList<>();
//            // TESTING: add k8s worker
//
//            for (Worker worker : workers) {
//                if (!worker.isAvailable()) {
//                    continue;
//                }
//
//                invokeWorker(worker, function, functionPayload);
//                // TODO: handle if no workers available
//            }

            String response = gson.toJson(invokeWorker(worker, function, functionPayload));

            LOGGER.log(Level.INFO, String.format("%s invocation response: %s", function.getName(), response));
            HttpHelper.sendResponse(exchange, 200, response);
        } catch (DBClientException ex) {
            // return error to client
            HttpHelper.sendResponse(exchange, 500, ex.getMessage());
        } catch (FunctionInvocationException ex) {
            // return function invocation error to client
            HttpHelper.sendResponse(exchange, ex.getHttpErrorCode(), ex.getMessage());
        } catch (Exception ex) {
            // return error to client
            HttpHelper.sendResponse(exchange, 500, ex.getMessage());
        }
    }

    private String invokeWorker(Worker worker, Function function, JsonObject functionPayload) throws WorkerException, IntegrationException {
        switch (worker.getHost().getName()) {
            case "KUBERNETES":
                return kubernetes.invokeFunction(function, functionPayload);
            case "AWS":
                return awsLambda.invokeFunction(function, functionPayload);
            default:
                throw new WorkerException("The host of the function's selected worker could not be found");
        }
    }
}
