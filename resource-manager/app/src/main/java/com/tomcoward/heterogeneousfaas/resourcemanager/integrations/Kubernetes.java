package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.google.common.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.proto.V1Batch.Job;
import io.kubernetes.client.proto.V1Batch.JobSpec;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            this.client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            this.api = new CoreV1Api(client);

            discoverPods();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error initialising Kubernetes integration", ex);
            throw new IntegrationException("There was an error connecting to the Kubernetes cluster");
        }
    }


    public Function createFunction(Function function) throws IntegrationException {
        // no need to do anything here... can spin up k8s job on the fly
        return function;
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        // read Dockerfile for function's runtime
        byte[] dockerfileBytes;
        try {
            switch (function.getSourceCodeRuntime().getImage()) {
                case "amazoncorretto:8":
                    Path dockerfilePath = Paths.get("./resources/docker/JDK8/Dockerfile");
                    dockerfileBytes = Files.readAllBytes(dockerfilePath);

                default:
                    throw new IntegrationException("No Dockerfile to invoke the function's container could be found - its source code runtime may be unsupported");
            }
        } catch (IOException ex) {
            throw new IntegrationException("The Dockerfile couldn't be read");
        }
        ByteString dockerfileBytestring = ByteString.copyFrom(dockerfileBytes);

        // get .jar file for function's source code


        V1.Container container = V1.Container.newBuilder()
                .setImageBytes(dockerfileBytestring)
                .build();

        V1.PodSpec podSpec = V1.PodSpec.newBuilder()
                .setContainers(0, container)
                .build();

        V1.PodTemplateSpec podTemplateSpec = V1.PodTemplateSpec.newBuilder()
                .setSpec(podSpec)
                .build();

        JobSpec jobSpec = JobSpec.newBuilder()
                .setTemplate(podTemplateSpec)
                .build();

        Job job = Job.newBuilder()
                .setSpec(jobSpec)
                .build();

        // TODO: get job output
        //OutputStream jobOutput;
        //try {
        //    job.writeTo(jobOutput);
        //} catch (IOException ex) {
        //    throw new IntegrationException("The K8s job output couldn't be read");
        //}

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
                V1Pod pod = (V1Pod) item.object;
                if (pod == null) {
                    continue;
                }

                pods.add(item.object);
                System.out.printf("POD %s STATUS : %s", pod.getKind(), pod.getStatus().getMessage());
            }
        } catch (ApiException ex) {
            LOGGER.log(Level.SEVERE, "Error discovering k8s pods", ex);
            throw new IntegrationException("There was an error with Kubernetes service discovery");
        }
    }
}
