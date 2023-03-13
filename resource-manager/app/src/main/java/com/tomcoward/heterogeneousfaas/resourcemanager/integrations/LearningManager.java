package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LearningManager {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String LEARNING_MANAGER_URI = "http://localhost:5000";

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

            if (httpResponse.statusCode() != 202) {
                throw new Exception("Learning Manager returned non-202 status code");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /train", ex);
            throw new IntegrationException("There was an error triggering training by the Learning Manager");
        }
    }

    public ArrayList<FunctionInvocationPrediction> getPredictions(String functionName, int inputSize) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/predictions/%s", LEARNING_MANAGER_URI, functionName)))
                    .method("PUT", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new Exception("Learning Manager returned non-200 status code");
            }

            String body = httpResponse.body();
            System.out.println(body);

            ArrayList<FunctionInvocationPrediction> predictions = new ArrayList<>();

            return predictions;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Learning Manager /train", ex);
            throw new IntegrationException("There was an error triggering training by the Learning Manager");
        }
    }


    public class FunctionInvocationPrediction {
        private final String worker;
        private final long duration;


        public FunctionInvocationPrediction(String worker, long duration) {
            this.worker = worker;
            this.duration = duration;
        }

        public String getWorker() {
            return worker;
        }

        public long getDuration() {
            return duration;
        }
    }
}
