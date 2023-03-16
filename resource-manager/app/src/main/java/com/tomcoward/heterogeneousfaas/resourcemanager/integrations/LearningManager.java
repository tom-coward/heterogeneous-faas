package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.google.gson.Gson;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LearningManager {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String LEARNING_MANAGER_URI = "http://localhost:5000";

    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public LearningManager() {
        this.httpClient = HttpClient.newBuilder().build();
    }


    public void triggerTraining(String functionName) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/train/%s", LEARNING_MANAGER_URI, functionName)))
                    .method("PUT", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 202) {
                throw new IntegrationException("Learning Manager returned non-202 status code");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /train", ex);
            throw new IntegrationException("There was an error triggering training by the Learning Manager");
        }
    }

    public void triggerIncrementalTraining(String functionName, String worker, int inputSize, float duration) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/train/incremental/%s?worker=%s&inputSize=%d&duration=%6.2f", LEARNING_MANAGER_URI, functionName, worker, inputSize, duration)))
                    .method("PUT", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 202) {
                throw new IntegrationException("Learning Manager returned non-202 status code");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /train/incremental", ex);
            throw new IntegrationException("There was an error triggering incremental training by the Learning Manager");
        }
    }

    public HashMap<String, Float> getPredictions(String functionName, int inputSize) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/predictions/%s?inputSize=%d", LEARNING_MANAGER_URI, functionName, inputSize)))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new IntegrationException("Learning Manager returned non-200 status code");
            }

            String body = httpResponse.body();

            HashMap predictions = new HashMap<String, Float>();
            predictions = gson.fromJson(body, predictions.getClass());

            return predictions;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /predictions", ex);
            throw new IntegrationException("There was an error getting predicted execution times from the Learning Manager");
        }
    }
}
