package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.callbacks.BuildImageCallback;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.callbacks.PushImageCallback;
import java.io.InputStream;
import java.time.Duration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Docker {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final DockerHttpClient dockerHttpClient;
    private final DockerClient dockerClient;

    public Docker() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        this.dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        this.dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }


    public void buildImage(InputStream containerInputStream, String tag) {
        try {
            dockerClient.buildImageCmd(containerInputStream)
                    .withTags(Set.of(tag))
                    .exec(new BuildImageCallback())
                    .awaitCompletion();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Building Docker image was interrupted");
            buildImage(containerInputStream, tag); // retry
        }
    }

    public String pushImageToRegistry(String imageId) {
        try {
            String imageNameWithRepository = String.format("%s/%s", dockerClient.authConfig().getRegistryAddress(), imageId);
            tagImage(imageId, imageNameWithRepository, imageNameWithRepository);

            dockerClient.pushImageCmd(imageNameWithRepository)
                    .exec(new PushImageCallback())
                    .awaitCompletion();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, String.format("Pushing Docker image \"%s\" to registry was interrupted", tag));
            pushImageToRegistry(imageId); // retry
        }
    }

    public void tagImage(String imageId, String imageNameWithRepository, String tag) {
        dockerClient.tagImageCmd(imageId, imageNameWithRepository, tag)
                .exec();
    }
}
