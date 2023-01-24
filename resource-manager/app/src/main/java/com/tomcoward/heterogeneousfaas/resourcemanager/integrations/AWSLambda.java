package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsAsyncClient;
import software.amazon.awssdk.services.ecs.model.*;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;

import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSLambda implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Region AWS_REGION = Region.EU_WEST_1;

    private final LambdaAsyncClient lambdaClient;

    public AWSLambda() {
        this.lambdaClient = LambdaAsyncClient.builder()
                .region(AWS_REGION)
                .build();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            // TODO: upload source code to s3 to pass as FunctionCode to Lambda

            FunctionCode functionCode = FunctionCode.builder()
                    .s3Key("")
                    .build();

            CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
                    .functionName(function.getName())
                    .runtime(function.getSourceCodeRuntime().toString())
                    .code()
                    .handler(function.getSourceCodeHandler())
                    .packageType("Zip")
                    .build();

            try {
                lambdaClient.createFunction(createFunctionRequest).get();
            } catch (InterruptedException ex) {
                createFunction(function); // retry
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, String.format("Error creating AWS Lambda function %s", function.getName()), ex);
                throw new IntegrationException("There was an issue with AWS Lambda");
            }

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
}
