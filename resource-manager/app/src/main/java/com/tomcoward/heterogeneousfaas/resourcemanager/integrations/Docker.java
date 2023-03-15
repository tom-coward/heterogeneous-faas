package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Docker {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String PYTHON_DOCKERFILE =
            "FROM public.ecr.aws/lambda/python:3.8-arm64\n" +
            "\n" +
            "# Copy application code into the container\n" +
            "COPY . ${LAMBDA_TASK_ROOT}\n" +
            "\n" +
            "# Set the command to be run when the container starts\n" +
            "CMD [\"main.handler\"]\n";

    private final DefaultDockerClientConfig config;
    private final DockerClient client;
    private final AWSECR awsEcr;

    public Docker(AWSECR awsEcr) {
        config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        client = DockerClientImpl.getInstance(config, httpClient);

        this.awsEcr = awsEcr;
    }


    public Function buildAndPushImage(Function function) throws IntegrationException {
        try {
            String tempDirPath = writeTempFiles(function.getName(), function.getSourceCode());

            String imageId = buildDockerImage(tempDirPath, function.getName());
            String containerUri = pushDockerImageToEcr(function.getName(), imageId);

            function.setContainerRegistryUri(containerUri);

            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating Docker image", ex);
            throw new IntegrationException("Error creating Docker image");
        }
    }

    private String writeTempFiles(String functionName, String sourceCode) throws IOException {
        Path tempDir = Files.createTempDirectory(String.format("%s-%s", functionName, UUID.randomUUID().toString()));

        // write source code to .py file in temp directory
        File sourceCodeFile = new File(tempDir.toFile(), "main.py");
        FileWriter writer = new FileWriter(sourceCodeFile);
        writer.write(sourceCode);
        writer.close();

        // write Dockerfile to temp directory
        File dockerfileFile = new File(tempDir.toFile(), "Dockerfile");
        writer = new FileWriter(dockerfileFile);
        writer.write(PYTHON_DOCKERFILE);
        writer.close();

        return tempDir.toString();
    }

    private String buildDockerImage(String tempDirPath, String functionName) throws InterruptedException, IntegrationException {
        String[] imageId = new String[1];
        final boolean[] buildSuccessful = {false};

        client.buildImageCmd(new File(tempDirPath))
                .exec(new ResultCallback.Adapter<BuildResponseItem>() {
                    @Override
                    public void onError(Throwable throwable) {
                        LOGGER.log(Level.SEVERE, String.format("Error building Docker image: %s", throwable.getMessage()));

                        super.onError(throwable);
                    }

                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.isBuildSuccessIndicated()) {
                            imageId[0] = item.getImageId();
                            buildSuccessful[0] = true;
                        }

                        super.onNext(item);
                    }

                    @Override
                    public void onComplete() {
                        if (buildSuccessful[0]) {
                            LOGGER.log(Level.INFO, "Docker image built successfully");
                        } else {
                            LOGGER.log(Level.SEVERE, "Docker image build failed");
                        }

                        super.onComplete();
                    }
                })
                .awaitCompletion();

        if (!buildSuccessful[0]) {
            throw new IntegrationException("There was a problem building the Docker image");
        }

        return imageId[0];
    }

    private String pushDockerImageToEcr(String functionName, String imageId) throws InterruptedException, IntegrationException {
        // get AWS ECR auth config
        AuthConfig authConfig = getAuthConfig();

        // create ECR repository for function
        String repositoryUri = awsEcr.createRepository(functionName);

        // tag image with container URI
        client.tagImageCmd(imageId, repositoryUri, "latest")
                .withForce(true)
                .exec();

        String containerUri = String.format("%s:latest", repositoryUri);

        final boolean[] pushSuccessful = {false};
        client.pushImageCmd(containerUri)
                .withAuthConfig(authConfig)
                .exec(new ResultCallback.Adapter<PushResponseItem>() {
                    @Override
                    public void onError(Throwable throwable) {
                        LOGGER.log(Level.SEVERE, String.format("Error pushing Docker image to ECR: %s", throwable.getMessage()));

                        super.onError(throwable);
                    }

                    @Override
                    public void onNext(PushResponseItem item) {
                        if (item.isErrorIndicated()) {
                            LOGGER.log(Level.SEVERE, String.format("Error pushing Docker image: %s", item.getErrorDetail().getMessage()));
                            pushSuccessful[0] = false;
                        } else {
                            pushSuccessful[0] = true;
                        }

                        super.onNext(item);
                    }

                    @Override
                    public void onComplete() {
                        if (pushSuccessful[0]) {
                            LOGGER.log(Level.INFO, "Docker image pushed successfully");
                        } else {
                            LOGGER.log(Level.SEVERE, "Docker image build failed");
                        }

                        super.onComplete();
                    }
                })
                .awaitCompletion();

        if (!pushSuccessful[0]) {
            throw new IntegrationException("There was a problem pushing the Docker image");
        }

        return containerUri;
    }

    private AuthConfig getAuthConfig() {
        AWSECR.AWSECRCredentials ecrCredentials = awsEcr.getCredentials();

        return new AuthConfig()
                .withUsername(ecrCredentials.getUsername())
                .withPassword(ecrCredentials.getPassword())
                .withRegistryAddress(ecrCredentials.getRegistryUrl()
            );
    }
}
