package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.logging.Logger;

public class SetCredentialsHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void handleRequest(HttpServerExchange exchange) {
        // handle request asynchronously in new thread if currently blocking IO thread
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        try {
            JsonObject credentialsObject = HttpHelper.getRequestBody(exchange, "credentials");

            setSystemProperties(credentialsObject);

            String response = "Credentials were successfully updated";
            HttpHelper.sendResponse(exchange, 200, response);
        } catch (Exception ex) {
            String response = "There was an issue updating credentials";
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private void setSystemProperties(JsonObject credentialsObject) throws IOException {
        String awsAccessKeyId = credentialsObject.getString("aws_access_key_id");
        String awsSecretAccessKey = credentialsObject.getString("aws_secret_access_key");

        if (awsAccessKeyId == null || awsAccessKeyId == "") {
            throw new IOException("The provided aws_access_key_id was invalid");
        }

        if (awsSecretAccessKey == null || awsSecretAccessKey == "") {
            throw new IOException("The provided aws_secret_access_key was invalid");
        }

        System.setProperty("aws.accessKeyId", awsAccessKeyId);
        System.setProperty("aws.secretAccessKey", awsSecretAccessKey);
    }
}
