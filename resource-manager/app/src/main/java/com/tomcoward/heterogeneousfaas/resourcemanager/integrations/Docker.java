package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

public class Docker {
    private final DockerHttpClient dockerHttpClient;

    public Docker() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        this.dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
    }

    public void buildImage(String containerPath, String tag) throws IOException {
        FileInputStream containerInputStream = new FileInputStream(containerPath);

        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path(String.format("/build?t=%s", tag))
                .body(containerInputStream)
                .build();

        containerInputStream.close();

        dockerHttpClient.execute(request).close();
    }

    public void pushImageToECR(String imageName, String imageTag) throws IOException {
        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path(String.format("/images/%s/push?tag=%s", imageName, imageTag))
                .build();

        dockerHttpClient.execute(request).getBody().close();
    }
}
