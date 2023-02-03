package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import io.fabric8.knative.client.DefaultKnativeClient;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.knative.serving.v1.ServiceBuilder;
import io.fabric8.knative.serving.v1.ServiceSpec;
import io.fabric8.knative.serving.v1.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import javax.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Kubernetes implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String KNATIVE_NAMESPACE = "default";

    private final KnativeClient knativeClient;

    public Kubernetes() throws IntegrationException {
        try {
            this.knativeClient = new DefaultKnativeClient();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initialising Kubernetes integration", ex);
            throw new IntegrationException("There was an error connecting to the Kubernetes cluster");
        }
    }


    public Function createFunction(Function function) throws IntegrationException {
        String serviceName = createKnativeService(function.getContainerPath());

        function.setEdgeKnServiceName(serviceName);

        return function;
    }

    public String invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        // TODO
        invokeKnativeService();
        return "";
    }


    private String createKnativeService(String containerRegistryUri) throws IntegrationException {
        Container serviceSpecContainer = new ContainerBuilder()
                .withImage(containerRegistryUri)
                .build();

        ServiceSpec serviceSpec = new ServiceSpecBuilder()
                .withNewTemplate()
                .withNewSpec()
                .withContainers(serviceSpecContainer)
                .endSpec()
                .endTemplate()
                .build();

        Service service = new ServiceBuilder()
                .withSpec(serviceSpec)
                .build();

        knativeClient.services().inNamespace(KNATIVE_NAMESPACE).resource(service).create();

        return service.getMetadata().getName();
    }

    private void invokeKnativeService() {

    }
}
