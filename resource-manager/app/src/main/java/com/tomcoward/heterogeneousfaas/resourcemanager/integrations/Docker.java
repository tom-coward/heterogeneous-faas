package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
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

            buildDockerImage(tempDirPath, function.getName());
            String containerUri = pushDockerImageToEcr(function.getName());

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

    private void buildDockerImage(String tempDirPath, String functionName) {
        BuildImageCmd buildCmd = client.buildImageCmd(new File(tempDirPath));
        buildCmd.withTag(String.format("%s:latest", functionName));

        buildCmd.exec(new ResultCallback.Adapter() {
            @Override
            public void onError(Throwable throwable) {
                LOGGER.log(Level.SEVERE, String.format("Error building Docker image: %s", throwable.getMessage()));
            }
        });
    }

    private String pushDockerImageToEcr(String functionName) {
        authDockerWithEcr();

        // create ECR repository for function
        String repositoryUri = awsEcr.createRepository(functionName);

        String containerUri = String.format("%s/%s:latest", repositoryUri, functionName);

        client.pushImageCmd(containerUri)
                .exec(new ResultCallback.Adapter<PushResponseItem>() {
                    @Override
                    public void onError(Throwable throwable) {
                        LOGGER.log(Level.SEVERE, String.format("Error pushing Docker image to ECR: %s", throwable.getMessage()));
                    }
                });

        return containerUri;
    }

    private void authDockerWithEcr() {
        AWSECR.AWSECRCredentials ecrCredentials = awsEcr.getCredentials();

        client.authCmd()
            .withAuthConfig(new AuthConfig()
                .withUsername(ecrCredentials.getUsername())
                .withPassword(ecrCredentials.getPassword())
                .withRegistryAddress(ecrCredentials.getRegistryUrl())
            )
            .exec();
    }
}
