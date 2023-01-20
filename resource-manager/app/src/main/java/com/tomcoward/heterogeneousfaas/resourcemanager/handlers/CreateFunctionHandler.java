package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSFargate;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

import javax.json.JsonObject;

public class CreateFunctionHandler implements com.sun.net.httpserver.HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSFargate awsFargate;
    private final Kubernetes kubernetes;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, AWSFargate awsFargate, Kubernetes kubernetes) {
        this.functionsRepo = functionsRepo;
        this.awsFargate = awsFargate;
        this.kubernetes = kubernetes;
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject functionObject = HttpHandler.getRequestBody(exchange, "function");

            Function function = createFunction(functionObject);

            String response = gson.toJson(function);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream responseStream = exchange.getResponseBody();
            responseStream.write(response.getBytes());
            responseStream.close();
        } catch (DBClientException ex) {
            // return error to client
            String response = "There was an issue saving your function";
            HttpHandler.sendResponse(exchange, response);
        } catch (Exception ex) {
            // return error to client
            String response = "There was an issue creating your function";
            HttpHandler.sendResponse(exchange, response);
        }
    }


    private Function createFunction(JsonObject functionObject) throws IOException, DBClientException, IntegrationException {
        // get function details from request body
        String name = functionObject.getString("name");
        byte[] sourceCode = functionObject.getString("source_code").getBytes();
        String sourceCodeHandler = functionObject.getString("source_code_handler");
        Function.SourceCodeRuntime sourceCodeRuntime = Function.SourceCodeRuntime.valueOf(functionObject.getString("source_code_runtime"));
        boolean edgeSupported = functionObject.getBoolean("edge_supported");
        boolean cloudAWSSupported = functionObject.getBoolean("cloud_aws_supported");

        Function function = new Function(name, sourceCode, sourceCodeHandler, sourceCodeRuntime, edgeSupported, cloudAWSSupported);

        // if aws supported, list in AWS Lambda
        if (function.isCloudAWSSupported()) {
            function = awsFargate.createFunction(function);
        }

        // if edge supported, add to kubernetes
        if (function.isEdgeSupported()) {
            function = kubernetes.createFunction(function);
        }

        // save function in database
        functionsRepo.create(function);

        return function;
    }
}
