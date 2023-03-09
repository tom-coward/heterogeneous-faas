package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LearningManager {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String LEARNING_MANAGER_URI = "http://localhost:8080";

    private final HttpClient httpClient;

    public LearningManager() {
        this.httpClient = HttpClient.newBuilder().build();
    }


    public void triggerTraining(String functionName) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/train/%s", LEARNING_MANAGER_URI, functionName)))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /train", ex);
            throw new IntegrationException("There was an error triggering training by the Learning Manager");
        }
    }
}
