package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.FunctionException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.WorkerException;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

public class InvokeFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
            InputStream requestBody = exchange.getRequestBody();

            // deserialize json from request body
            JsonReader jsonReader = Json.createReader(requestBody);
            JsonObject jsonObject = jsonReader.readObject();
            requestBody.close();
            jsonReader.close();

            // get name of function to be invoked
            String functionName = jsonObject.getString("function_name");
            if (functionName == null || functionName.trim().isEmpty()) {
                throw new FunctionException(String.format("No function with the name %s exists to be invoked", functionName));
            }

            // get payload of function (if any)
            JsonObject functionPayload = jsonObject.getJsonObject("function_payload");

            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload.toString()));

            Function function = functionsRepo.get(functionName);

            // invoke in AWS
            Worker awsWorker = new Worker(Worker.Host.CLOUD_AWS, Worker.Status.AVAILABLE);
            String response = invokeWorker(awsWorker, function, functionPayload);

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

            HttpHelper.sendResponse(exchange, 200, response);
        } catch (DBClientException ex) {
            // TODO: return error to client
            return;
        } catch (Exception ex) {
            // TODO: return error to client
            return;
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
