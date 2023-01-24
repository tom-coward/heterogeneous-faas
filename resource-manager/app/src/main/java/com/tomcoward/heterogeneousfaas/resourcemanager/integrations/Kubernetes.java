package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import javax.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kubernetes implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final ApiClient client;
    private final CoreV1Api api;

    public Kubernetes() throws IntegrationException {
        try {
            this.client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            this.api = new CoreV1Api(client);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initialising Kubernetes integration", ex);
            throw new IntegrationException("There was an error connecting to the Kubernetes cluster");
        }
    }


    public Function createFunction(Function function) throws IntegrationException {
        // TODO
        return function;
    }

    public String invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        // TODO
        return "";
    }
}
