package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ecs.EcsAsyncClient;
import software.amazon.awssdk.services.ecs.model.ContainerDefinition;
import software.amazon.awssdk.services.ecs.model.CreateClusterRequest;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionResponse;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSFargate implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String AWS_IAM_ROLE_ARN = "";
    private final static String ECS_CLUSTER_NAME = "heterogeneous-faas";

    private final EcsAsyncClient fargateClient;

    public AWSFargate() {
        this.fargateClient = EcsAsyncClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            createECSCluster();

            createTaskDefinition(function.getName(), function.getSourceCodeRuntime());

            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS Lambda function", ex);
            throw new IntegrationException("There was an issue creating the function on AWS");
        }
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        try {
            return functionPayload;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error invoking AWS Lambda function", ex);
            throw new IntegrationException("There was an issue invoking the function");
        }
    }


    private void createECSCluster() throws IntegrationException {
        CreateClusterRequest createClusterRequest = CreateClusterRequest.builder()
                .clusterName(ECS_CLUSTER_NAME)
                .build();

        try {
            fargateClient.createCluster(createClusterRequest).get();
        } catch (InterruptedException ex) {
            createECSCluster(); // retry
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS ECS cluster", ex);
            throw new IntegrationException("There was an issue with AWS ECS (Fargate)");
        }
    }

    private void createTaskDefinition(String functionName, Function.SourceCodeRuntime runtime) throws IntegrationException {
        ContainerDefinition containerDefinition = ContainerDefinition.builder()
                .name(functionName)
                .image(runtime.getImage())
                .build();

        RegisterTaskDefinitionRequest registerTaskDefinitionRequest = RegisterTaskDefinitionRequest.builder()
                .family(functionName)
                .containerDefinitions(containerDefinition)
                .build();

        try {
            RegisterTaskDefinitionResponse registerTaskDefinitionResponse = fargateClient.registerTaskDefinition(registerTaskDefinitionRequest).get();
        } catch (InterruptedException ex) {
            createTaskDefinition(functionName, runtime); // retry
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS ECS cluster", ex);
            throw new IntegrationException("There was an issue with AWS ECS (Fargate)");
        }
    }
}
