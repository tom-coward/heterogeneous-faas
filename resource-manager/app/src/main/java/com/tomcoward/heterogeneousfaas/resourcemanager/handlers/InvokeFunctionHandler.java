package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.LearningManager;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.FunctionExecution;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionExecutionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

public class InvokeFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IFunctionRepository functionsRepo;
    private final IFunctionExecutionRepository functionExecutionsRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;
    private final LearningManager learningManager;

    public InvokeFunctionHandler(IFunctionRepository functionsRepo, IFunctionExecutionRepository functionExecutionsRepo, AWSLambda awsLambda, Kubernetes kubernetes, LearningManager learningManager) {
        this.functionsRepo = functionsRepo;
        this.functionExecutionsRepo = functionExecutionsRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;
        this.learningManager = learningManager;
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
            String functionPayload = requestBody.getJsonObject("function_payload").toString();

            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload));

            FunctionInvocationResponse response = invokeFunction(functionName, functionPayload);

            HttpHelper.sendResponse(exchange, 200, response.getResponse());

            recordFunctionExecution(functionName, response.getWorker(), functionPayload.length(), response);
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

    private FunctionInvocationResponse invokeFunction(String functionName, String functionPayload) throws DBClientException, WorkerException, IntegrationException {
        Function function = functionsRepo.get(functionName);

        // invoke in AWS
        String worker = AWSLambda.WORKER_NAME;
        float predictedDuration = 0;

        // invoke in Kubernetes
        //Worker worker = new Worker(Worker.HOST.KUBERNETES, true);

        // call ML Manager to choose worker
        //ArrayList<LearningManager.FunctionInvocationPrediction> predictions = learningManager.getPredictions(function.getName(), functionPayload.length());

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

        FunctionInvocationResponse response = invokeWorker(worker, function, functionPayload, predictedDuration);

        LOGGER.log(Level.INFO, String.format("%s invocation response in %dms: %s", function.getName(), response.getDuration(), response.getResponse()));

        return response;
    }

    public FunctionInvocationResponse invokeWorker(String worker, Function function, String functionPayload, float predictedDuration) throws WorkerException, IntegrationException {
        Instant invocationStartTime = Instant.now();

        String response;

        switch (worker) {
            case "KUBERNETES":
                response = kubernetes.invokeFunction(function, functionPayload);
                break;
            case "AWS":
                response = awsLambda.invokeFunction(function, functionPayload);
                break;
            default:
                throw new WorkerException(String.format("The host of the function's selected worker (%s) could not be found", worker));
        }

        Instant invocationEndTime = Instant.now();

        float invocationDuration = Duration.between(invocationStartTime, invocationEndTime).toMillis();

        return new FunctionInvocationResponse(response, invocationDuration, predictedDuration, worker);
    }

    public void recordFunctionExecution(String functionName, String worker, int inputSize, FunctionInvocationResponse functionInvocationResponse) throws DBClientException {
        // if function response indicated there was an error, mark execution as unsuccessful
        boolean isSuccess = !functionInvocationResponse.getResponse().contains("errorType");

        FunctionExecution functionExecution = new FunctionExecution(functionName, worker, inputSize, functionInvocationResponse.getDuration(), functionInvocationResponse.getPredictedDuration(), isSuccess);

        functionExecutionsRepo.create(functionExecution);
    }


    public class FunctionInvocationResponse {
        private final String response;
        private final float duration;
        private final float predictedDuration;
        private final String worker;

        public FunctionInvocationResponse(String response, float duration, float predictedDuration, String worker) {
            this.response = response;
            this.duration = duration;
            this.predictedDuration = predictedDuration;
            this.worker = worker;
        }


        public String getResponse() {
            return this.response;
        }

        public float getDuration() {
            return this.duration;
        }

        public float getPredictedDuration() {
            return this.predictedDuration;
        }

        public String getWorker() {
            return this.worker;
        }
    }
}
