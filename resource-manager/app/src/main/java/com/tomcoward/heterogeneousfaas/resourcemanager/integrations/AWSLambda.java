package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import javax.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class AWSLambda implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Region AWS_REGION = Region.EU_WEST_1;
    private final static String AWS_IAM_ROLE_NAME = "heterogeneous-faas-lambda-role";

    private final LambdaClient lambdaClient;
    private final IamClient iamClient;

    private final String awsIamRoleArn;

    public AWSLambda() throws IntegrationException {
        this.lambdaClient = LambdaClient.builder()
                .region(AWS_REGION)
                .build();
        this.iamClient = IamClient.builder()
                .region(AWS_REGION)
                .build();

        this.awsIamRoleArn = createIamRole();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            // create zip file containing function source code files
            ZipInputStream sourceCodeInputStream = new ZipInputStream(getClass().getClassLoader().getResourceAsStream(function.getSourceCodePath()));
            SdkBytes sourceCodeZipFile = SdkBytes.fromInputStream(sourceCodeInputStream);
            sourceCodeInputStream.close();

            FunctionCode functionCode = FunctionCode.builder()
                    .zipFile(sourceCodeZipFile)
                    .build();

            CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
                    .functionName(function.getName())
                    .runtime(function.getSourceCodeRuntime())
                    .code(functionCode)
                    .handler(function.getSourceCodeHandler())
                    .packageType("Zip")
                    .role(this.awsIamRoleArn)
                    .build();

            String lambdaFunctionArn = lambdaClient.createFunction(createFunctionRequest).functionArn();

            function.setCloudAwsArn(lambdaFunctionArn);

            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error creating AWS Lambda function %s", function.getName()), ex);
            throw new IntegrationException("There was an issue with AWS Lambda");
        }
    }

    public String invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        try {
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(function.getName())
                    .build();

            InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

            // return lambda function response payload as JsonObject
            return invokeResponse.payload().asUtf8String();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error invoking AWS Lambda function", ex);
            throw new IntegrationException("There was an issue invoking the function");
        }
    }

    private String createIamRole() throws IntegrationException {
        try {
            CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .assumeRolePolicyDocument(
                        "{\n" +
                        "    \"Version\": \"2012-10-17\",\n" +
                        "    \"Statement\": [\n" +
                        "        {\n" +
                        "            \"Effect\": \"Allow\",\n" +
                        "            \"Action\": [\n" +
                        "                \"sts:AssumeRole\"\n" +
                        "            ],\n" +
                        "            \"Principal\": {\n" +
                        "                \"Service\": [\n" +
                        "                    \"lambda.amazonaws.com\"\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"
                    )
                    .build();

            CreateRoleResponse createRoleResponse = iamClient.createRole(createRoleRequest);

            return createRoleResponse.role().arn();
        } catch (EntityAlreadyExistsException ex) {
            // role already exists, so get its arn
            GetRoleRequest getRoleRequest = GetRoleRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .build();

            GetRoleResponse getRoleResponse = iamClient.getRole(getRoleRequest);

            return getRoleResponse.role().arn();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS Lambda IAM role", ex);
            throw new IntegrationException("There was an issue setting up the AWS Lambda environment (IAM)");
        }
    }
}
