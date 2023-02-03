package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSECR {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String REPOSITORY_NAME = "heterogeneous-faas";
    private String repositoryUri;

    private final EcrClient ecrClient;
    private final Docker docker;

    public AWSECR() {
        this.ecrClient = EcrClient.builder()
                .region(AWSLambda.AWS_REGION)
                .build();
        this.docker = new Docker();

        createRepository();
    }


    public String publishContainer(String imageTag, String containerPath) throws IntegrationException {
        try {
            String tag = String.format("%s/%s", repositoryUri, imageTag);

            docker.buildImage(containerPath, tag);

            docker.pushImageToECR(imageTag, tag);

            return tag;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error building & pushing container image to AWS ECR", ex);
            throw new IntegrationException("There was an issue building/uploading the container image");
        }
    }

    private void createRepository() {
        // check if repository already exists
        if (ecrClient.describeRepositories().repositories().stream().filter(x -> x.repositoryName().equals(REPOSITORY_NAME)).findFirst().isPresent()) {
            return;
        }

        CreateRepositoryRequest createRepositoryRequest = CreateRepositoryRequest.builder()
                .repositoryName(REPOSITORY_NAME)
                .build();

        this.repositoryUri = ecrClient.createRepository(createRepositoryRequest).repository().repositoryUri();
    }
}
