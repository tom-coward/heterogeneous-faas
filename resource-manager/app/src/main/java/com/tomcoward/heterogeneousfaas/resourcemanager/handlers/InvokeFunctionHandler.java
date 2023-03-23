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

            // get payload of function (if any)
            JsonArray functionPayload = requestBody.getJsonArray("function_payload");

            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
            LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload.toString()));

            FunctionInvocationResponse response = invokeFunction(functionName, functionPayload);

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

    private FunctionInvocationResponse invokeFunction(String functionName, JsonArray functionPayload) throws DBClientException, WorkerException, IntegrationException {
        Function function = functionsRepo.get(functionName);

        // get predicted durations on each worker from Learning Manager
        HashMap<String, Double> predictions = learningManager.getPredictions(function.getName(), functionPayload.size());

        // invoke worker with lowest predicted duration
        Map.Entry selectedPrediction = predictions.entrySet().stream().sorted(Map.Entry.comparingByValue()).findFirst().get();
        if (selectedPrediction == null) {
            throw new FunctionInvocationException(String.format("No workers were found to execute function %s", function.getName()), 503);
        }

        String worker = (String) selectedPrediction.getKey();
        double predictedDuration = (double) selectedPrediction.getValue();

        String functionPayloadString = functionPayload.toString();

        LOGGER.log(Level.INFO, String.format("Invoking function %s on worker %s", function.getName(), worker));

        FunctionInvocationResponse response;
        try {
            response = invokeWorker(worker, function, functionPayloadString, predictedDuration);
        } catch (CapacityException ex) {
            // if worker capacity exceeded, invoke function on other worker
            String otherWorker = worker.equals("KUBERNETES") ? "AWS" : "KUBERNETES";
            LOGGER.log(Level.INFO, String.format("Invoking function %s on worker %s", function.getName(), otherWorker));
            response = invokeWorker(otherWorker, function, functionPayloadString, predictedDuration);
        }

        LOGGER.log(Level.INFO, String.format("%s invocation response in %6.2fms (predicted: %6.2fms): %s", function.getName(), response.getDuration(), response.getPredictedDuration(), response.getResponse()));

        return response;
    }

    public FunctionInvocationResponse invokeWorker(String worker, Function function, String functionPayload, double predictedDuration) throws WorkerException, IntegrationException {
        Instant invocationStartTime = Instant.now();

        String response;

        try {
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
        } catch (CapacityException ex) {
            LOGGER.log(Level.INFO, String.format("%s worker capacity exceeded, retrying with different worker", worker));
            throw ex;
        }

        Instant invocationEndTime = Instant.now();

        double invocationDuration = Duration.between(invocationStartTime, invocationEndTime).toMillis();

        return new FunctionInvocationResponse(response, invocationDuration, predictedDuration, worker);
    }

    public void recordFunctionExecution(String functionName, String worker, int inputSize, FunctionInvocationResponse functionInvocationResponse, boolean isTraining) throws DBClientException, IntegrationException {
        // if function response indicated there was an error, mark execution as unsuccessful
        boolean isSuccess = !functionInvocationResponse.getResponse().contains("errorType");

        FunctionExecution functionExecution = new FunctionExecution(functionName, worker, inputSize, functionInvocationResponse.getDuration(), functionInvocationResponse.getPredictedDuration(), isSuccess);

        functionExecutionsRepo.create(functionExecution);

        if (!isTraining) {
            learningManager.triggerIncrementalTraining(functionName, worker, inputSize, functionInvocationResponse.getDuration());
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
