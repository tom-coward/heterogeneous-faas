package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

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


    public void handleRequest(HttpServerExchange exchange) {
        // handle request asynchronously in new thread if currently blocking IO thread
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        try {
            JsonObject requestBody = HttpHelper.getRequestBody(exchange, null);

            // get name of function to be invoked
            String functionName = requestBody.getString("function_name");
            if (functionName == null || functionName.trim().isEmpty()) {
                throw new FunctionException(String.format("No function with the name %s exists to be invoked", functionName));
            }

            // get worker to execute on (if specified)
            String worker;
            try {
                worker = requestBody.getString("worker");
            } catch (NullPointerException ex) {
                // worker not specified
                worker = null;
            }

            // get payload of function (if any)
            JsonArray functionPayload = requestBody.getJsonArray("function_payload");

            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload.toString()));

            FunctionInvocationResponse response = invokeFunction(functionName, functionPayload, worker);

            HttpHelper.sendResponse(exchange, 200, response.getResponse());

            recordFunctionExecution(functionName, response.getWorker(), functionPayload.size(), response, false);
        } catch (DBClientException ex) {
            LOGGER.log(Level.SEVERE, "Error invoking function", ex);
            // return error to client
            HttpHelper.sendResponse(exchange, 500, "There was a problem invoking the function");
        } catch (FunctionInvocationException ex) {
            LOGGER.log(Level.SEVERE, "Error creating function", ex);
            // return function invocation error to client
            HttpHelper.sendResponse(exchange, ex.getHttpErrorCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating function", ex);
            // return error to client
            HttpHelper.sendResponse(exchange, 500, "There was a problem invoking the function");
        }
    }

    private FunctionInvocationResponse invokeFunction(String functionName, JsonArray functionPayload, String worker) throws DBClientException, WorkerException, IntegrationException, CapacityException {
        Function function = functionsRepo.get(functionName);

        double predictedDuration;

        // get predicted durations on each worker from Learning Manager
        HashMap<String, Double> predictions = learningManager.getPredictions(function.getName(), functionPayload.size());

        if (worker != null && !worker.trim().isEmpty()) {
            // worker has been defined by end user
            LOGGER.log(Level.INFO, String.format("%s was specified as worker to execute on", worker));

            // get predicted duration of selected worker
            predictedDuration = predictions.get(worker);
        } else {
            // worker not defined, so decide worker based on predictions

            // invoke worker with lowest predicted duration
            Map.Entry selectedPrediction = predictions.entrySet().stream().sorted(Map.Entry.comparingByValue()).findFirst().get();
            if (selectedPrediction == null) {
                throw new FunctionInvocationException(String.format("No workers were found to execute function %s", function.getName()), 503);
            }

            worker = (String) selectedPrediction.getKey();
            predictedDuration = (double) selectedPrediction.getValue();
        }

        String functionPayloadString = functionPayload.toString();

        LOGGER.log(Level.INFO, String.format("Invoking function %s on worker %s", function.getName(), worker));

        FunctionInvocationResponse response = invokeWorker(worker, function, functionPayloadString, predictedDuration);

        LOGGER.log(Level.INFO, String.format("%s invocation response in %6.2fms (predicted: %6.2fms): %s", function.getName(), response.getDuration(), response.getPredictedDuration(), response.getResponse()));

        return response;
    }

    public FunctionInvocationResponse invokeWorker(String worker, Function function, String functionPayload, double predictedDuration) throws WorkerException, IntegrationException {
        Instant invocationStartTime;
        Instant invocationEndTime;

        String response;

        switch (worker) {
            case "KUBERNETES":
                invocationStartTime = Instant.now();
                response = kubernetes.invokeFunction(function, functionPayload);
                invocationEndTime = Instant.now();
                break;
            case "AWS":
                invocationStartTime = Instant.now();
                response = awsLambda.invokeFunction(function, functionPayload);
                invocationEndTime = Instant.now();
                break;
            default:
                throw new WorkerException(String.format("The host of the function's selected worker (%s) could not be found", worker));
        }

        double invocationDuration = Duration.between(invocationStartTime, invocationEndTime).toMillis();

        return new FunctionInvocationResponse(response, invocationDuration, predictedDuration, worker);
    }

    public void recordFunctionExecution(String functionName, String worker, int inputSize, FunctionInvocationResponse functionInvocationResponse, boolean isTraining) throws DBClientException, IntegrationException {
        // if function response indicated there was an error, mark execution as unsuccessful
        boolean isSuccess = !functionInvocationResponse.getResponse().contains("errorType");

        FunctionExecution functionExecution = new FunctionExecution(functionName, worker, inputSize, functionInvocationResponse.getDuration(), functionInvocationResponse.getPredictedDuration(), isSuccess);

        functionExecutionsRepo.create(functionExecution);

        if (!isTraining) {
            //learningManager.triggerIncrementalTraining(functionName, worker);
        }
    }


    public class FunctionInvocationResponse {
        private final String response;
        private final double duration;
        private final double predictedDuration;
        private final String worker;

        public FunctionInvocationResponse(String response, double duration, double predictedDuration, String worker) {
            this.response = response;
            this.duration = duration;
            this.predictedDuration = predictedDuration;
            this.worker = worker;
        }


        public String getResponse() {
            return this.response;
        }

        public double getDuration() {
            return this.duration;
        }

        public double getPredictedDuration() {
            return this.predictedDuration;
        }

        public String getWorker() {
            return this.worker;
        }
    }
}
