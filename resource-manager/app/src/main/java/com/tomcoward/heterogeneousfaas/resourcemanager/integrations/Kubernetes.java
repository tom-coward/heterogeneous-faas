package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.google.common.reflect.TypeToken;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kubernetes implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final ApiClient client;
    private final CoreV1Api api;

    private ArrayList<V1Pod> pods = new ArrayList<>();

    public Kubernetes() throws IntegrationException {
        try {
            this.client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);
            this.api = new CoreV1Api();

            discoverPods();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initialising Kubernetes integration", ex);
            throw new IntegrationException("There was an error connecting to the Kubernetes cluster");
        }
    }


    public Function createFunction(Function function) throws IntegrationException {
        return function;
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        return functionPayload;
    }

    private void discoverPods() throws IntegrationException {
        try {
            Watch<V1Pod> watch = Watch.createWatch(
                    client,
                    api.listPodForAllNamespacesCall(true, null, null, null, null, null, null, null, null, null, null),
                    new TypeToken<Watch.Response<V1Namespace>>() {
                    }.getType());

            for (Watch.Response<V1Pod> item : watch) {
                pods.add(item.object);
                System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
            }
        } catch (ApiException ex) {
            LOGGER.log(Level.SEVERE, "Error discovering k8s pods", ex);
            throw new IntegrationException("There was an error with Kubernetes service discovery");
        }
    }
}