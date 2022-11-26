package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class CreateFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

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
            InputStream requestBody = exchange.getRequestBody();

            // deserialize json from request body
            JsonReader jsonReader = Json.createReader(requestBody);
            JsonObject jsonObject = jsonReader.readObject();
            requestBody.close();
            jsonReader.close();

            // get function object
            JsonObject functionObject = jsonObject.getJsonObject("function");

            createFunction(functionObject);
        } catch (DBClientException ex) {
            // TODO: return error to client
            return;
        } catch (Exception ex) {
            // TODO: return error to client
            return;
        }
    }


    private void createFunction(JsonObject functionObject) throws IOException, DBClientException, IntegrationException {
        Function function = new Function(functionObject);

        // if aws supported, list in AWS Lambda
        if (function.isCloudAWSSupported()) {
            function = awsLambda.createFunction(function);
        }

        // if edge supported, add to kubernetes
        if (function.isEdgeSupported()) {
            function = kubernetes.createFunction(function);
        }

        // save in database
        functionsRepo.create(function);
    }
}
