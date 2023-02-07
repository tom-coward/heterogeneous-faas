package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.logging.Logger;

public class SetCredentialsHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void handle(HttpExchange exchange) throws IOException {
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
