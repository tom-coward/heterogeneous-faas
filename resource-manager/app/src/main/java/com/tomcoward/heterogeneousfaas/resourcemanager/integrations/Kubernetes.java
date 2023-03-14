package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.FunctionInvocationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import io.fabric8.knative.client.DefaultKnativeClient;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.knative.serving.v1.ServiceBuilder;
import io.fabric8.knative.serving.v1.ServiceSpec;
import io.fabric8.knative.serving.v1.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kubernetes implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public final static String WORKER_NAME = "KUBERNETES";

    private final static String KNATIVE_NAMESPACE = "default";
    private final static String KNATIVE_URI = "127.0.0.1.sslip.io/2015-03-31/functions/function/invocations";
    private final static String RESOURCE_CPU_LIMIT = "100m";
    private final static String RESOURCE_MEMORY_LIMIT = "128Mi";

    private final KnativeClient knativeClient;
    private final HttpClient httpClient;

    public Kubernetes() throws IntegrationException {
        try {
            this.knativeClient = new DefaultKnativeClient();
            this.httpClient = HttpClient.newBuilder().build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initialising Kubernetes integration", ex);
            throw new IntegrationException("There was an error connecting to the Kubernetes cluster");
        }
    }


    public Function createFunction(Function function) {
        String serviceUri = createKnativeService(function.getContainerRegistryUri(), function.getName());

        function.setEdgeKnServiceUri(serviceUri);

        return function;
    }

    public String invokeFunction(Function function, String functionPayload) throws IntegrationException {
        return invokeKnativeService(function.getEdgeKnServiceUri(), "POST", functionPayload.toString());
    }


    private String createKnativeService(String containerRegistryUri, String name) {
        ObjectMeta metadata = new ObjectMetaBuilder()
                .withName(name)
                .build();

        // can define resource limits of service pods (cpu & memory) here
        Container serviceSpecContainer = new ContainerBuilder()
                .withImage(containerRegistryUri)
                .withResources(new ResourceRequirementsBuilder()
                    .addToLimits("cpu", new Quantity(RESOURCE_CPU_LIMIT))
                    .addToLimits("memory", new Quantity(RESOURCE_MEMORY_LIMIT))
                    .build())
                .build();

        ServiceSpec serviceSpec = new ServiceSpecBuilder()
                .withNewTemplate()
                .withNewSpec()
                .withContainers(serviceSpecContainer)
                .endSpec()
                .endTemplate()
                .build();

        Service service = new ServiceBuilder()
                .withMetadata(metadata)
                .withSpec(serviceSpec)
                .build();

        knativeClient.services().inNamespace(KNATIVE_NAMESPACE).resource(service).create();

        return String.format("http://%s.%s.%s", service.getMetadata().getName(), KNATIVE_NAMESPACE, KNATIVE_URI);
    }

    private String invokeKnativeService(String serviceUri, String method, String functionPayload) throws IntegrationException {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(serviceUri))
                    .method(method, HttpRequest.BodyPublishers.ofString(functionPayload))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200 && httpResponse.statusCode() != 204) {
                if (httpResponse.statusCode() == 404) { // Knative service still being initialised... try again in 1 sec
                    Thread.sleep(1000);
                    return invokeKnativeService(serviceUri, method, functionPayload);
                }

                String errorMessage = String.format("Function invocation failed. Status code: %d, body: %s", httpResponse.statusCode(), httpResponse.body());
                LOGGER.log(Level.WARNING, errorMessage);
                throw new FunctionInvocationException(errorMessage, httpResponse.statusCode());
            }

            return httpResponse.body();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending HTTP request to Knative (k8s) service", ex);
            throw new IntegrationException("There was an error invoking the function");
        }
    }
}