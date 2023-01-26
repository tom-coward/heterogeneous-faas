package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import javax.json.JsonObject;

public class CreateFunctionHandler implements com.sun.net.httpserver.HttpHandler {
    private final Gson gson = new Gson();

    private final IFunctionRepository functionsRepo;
    private final AWSLambda awsFargate;
    private final Kubernetes kubernetes;

    public CreateFunctionHandler(IFunctionRepository functionsRepo, AWSLambda awsFargate, Kubernetes kubernetes) {
        this.functionsRepo = functionsRepo;
        this.awsFargate = awsFargate;
        this.kubernetes = kubernetes;
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject functionObject = HttpHelper.getRequestBody(exchange, "function");

            Function function = createFunction(functionObject);

            String response = gson.toJson(function);
            HttpHelper.sendResponse(exchange, 200, response);
        } catch (DBClientException ex) {
            // return error to client
            String response = "There was an issue saving your function";
            HttpHelper.sendResponse(exchange, 500, response);
        } catch (Exception ex) {
            // return error to client
            String response = "There was an issue creating your function";
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private Function createFunction(JsonObject functionObject) throws IOException, DBClientException, IntegrationException {
        // get function details from request body
        String name = functionObject.getString("name");
        ByteBuffer sourceCode = ByteBuffer.wrap(functionObject.getString("source_code").getBytes());
        String sourceCodeHandler = functionObject.getString("source_code_handler");
        String sourceCodeRuntime = functionObject.getString("source_code_runtime");
        boolean edgeSupported = functionObject.getBoolean("edge_supported");
        boolean cloudAWSSupported = functionObject.getBoolean("cloud_aws_supported");

        Function function = new Function(name, sourceCode, sourceCodeHandler, sourceCodeRuntime, edgeSupported, cloudAWSSupported);

        // if aws supported, add to AWS Fargate
        if (function.isCloudAWSSupported()) {
            function = awsFargate.createFunction(function);
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
