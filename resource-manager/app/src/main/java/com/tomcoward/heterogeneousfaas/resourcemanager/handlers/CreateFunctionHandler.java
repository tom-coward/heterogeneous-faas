package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import javax.json.JsonObject;

public class CreateFunctionHandler implements com.sun.net.httpserver.HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, AWSLambda awsLambda, Kubernetes kubernetes) {
        this.functionsRepo = functionsRepo;
        this.awsLambda = awsLambda;
        this.kubernetes = kubernetes;
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject functionObject = HttpHelper.getRequestBody(exchange, "function");

            Function function = createFunction(functionObject);

            LOGGER.log(Level.INFO, String.format("CreateFunctionHandler functionName input: \"%s\"", function.getName()));

            String response = gson.toJson(function);
            HttpHelper.sendResponse(exchange, 200, response);
        } catch (DBClientException ex) {
            // return error to client
            String response = "There was an issue saving your function";
            HttpHelper.sendResponse(exchange, 500, response);
        } catch (Exception ex) {
            // return error to client
            LOGGER.log(Level.SEVERE, "Error creating function", ex);
            String response = "There was an issue creating your function";
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private Function createFunction(JsonObject functionObject) throws DBClientException, IntegrationException {
        // get function details from request body
        String name = functionObject.getString("name");
        String sourceCodePath = functionObject.getString("source_code_path");
        String sourceCodeHandler = functionObject.getString("source_code_handler");
        String sourceCodeRuntime = functionObject.getString("source_code_runtime");
        boolean edgeSupported = functionObject.getBoolean("edge_supported");
        boolean cloudAWSSupported = functionObject.getBoolean("cloud_aws_supported");

        Function function = new Function(name, sourceCodePath, sourceCodeHandler, sourceCodeRuntime, edgeSupported, cloudAWSSupported);

        // if aws supported, add to AWS Fargate
        if (function.isCloudAWSSupported()) {
            function = awsLambda.createFunction(function);
        }

        // if edge supported, add to Kubernetes
        if (function.isEdgeSupported()) {
            function = kubernetes.createFunction(function);
        }

        // save function in database
        functionsRepo.create(function);

        return function;
    }
}
