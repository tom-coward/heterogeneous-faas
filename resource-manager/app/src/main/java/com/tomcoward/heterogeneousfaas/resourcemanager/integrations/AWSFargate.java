package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsAsyncClient;
import software.amazon.awssdk.services.ecs.model.*;
import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSFargate implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String ECS_CLUSTER_NAME = "heterogeneous-faas";
    private final static Region AWS_REGION = Region.EU_WEST_1;

    private final EcsAsyncClient fargateClient;

    public AWSFargate() {
        this.fargateClient = EcsAsyncClient.builder()
                .region(AWS_REGION)
                .credentialsProvider(SystemPropertyCredentialsProvider.create())
                .build();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            // following steps listed in: https://docs.aws.amazon.com/AmazonECS/latest/userguide/getting-started-fargate.html

            createECSCluster();

            String taskDefinitionArn = createTaskDefinition(function.getName(), function.getSourceCodeRuntime());

            function.setCloudAWSARN(taskDefinitionArn);

            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS Lambda function", ex);
            throw new IntegrationException("There was an issue creating the function on AWS");
        }
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        try {
            return startTask(function.getCloudAWSARN(), functionPayload.toString());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error invoking AWS Lambda function", ex);
            throw new IntegrationException("There was an issue invoking the function");
        }
    }


    private void createECSCluster() throws IntegrationException {
        // check if cluster already exists...
        try {
            DescribeClustersResponse describeClustersResponse = fargateClient.describeClusters().get();

            for (Cluster cluster : describeClustersResponse.clusters()) {
                if (cluster.clusterName().equals(ECS_CLUSTER_NAME)) {
                    return; // skip creating cluster as it already exists
                }
            }
        } catch (InterruptedException ex) {
            createECSCluster(); // retry
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error checking AWS ECS cluster already exists", ex);
            throw new IntegrationException("There was an issue with AWS ECS (Fargate)");
        }

        // if it doesn't already exist, create it...
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

    private String createTaskDefinition(String functionName, Function.SourceCodeRuntime runtime) throws IntegrationException {
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
            return registerTaskDefinitionResponse.taskDefinition().taskDefinitionArn();
        } catch (InterruptedException ex) {
            return createTaskDefinition(functionName, runtime); // retry
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS ECS cluster", ex);
            throw new IntegrationException("There was an issue with AWS ECS (Fargate)");
        }
    }

    private JsonObject startTask(String functionCloudAWSARN, String functionPayload) throws IntegrationException {
        StartTaskRequest startTaskRequest = StartTaskRequest.builder()
                .taskDefinition(functionCloudAWSARN)
                .build();

        try {
            StartTaskResponse startTaskResponse = fargateClient.startTask(startTaskRequest).get();

            List<Task> tasks = startTaskResponse.tasks();

            // TODO: return task (function) response
            return JsonObject.EMPTY_JSON_OBJECT;
        } catch (InterruptedException ex) {
            return startTask(functionCloudAWSARN, functionPayload); // retry
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS ECS cluster", ex);
            throw new IntegrationException("There was an issue with AWS ECS (Fargate)");
        }
    }
}
