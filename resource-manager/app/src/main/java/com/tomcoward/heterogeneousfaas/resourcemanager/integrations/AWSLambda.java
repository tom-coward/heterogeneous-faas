package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import javax.json.JsonObject;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSLambda implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public final static Region AWS_REGION = Region.EU_WEST_1;

    private final LambdaClient lambdaClient;

    private final AWSIAM awsIam;

    private final String awsIamRoleArn;

    public AWSLambda(AWSIAM awsIam) throws IntegrationException {
        this.lambdaClient = LambdaClient.builder()
                .region(AWS_REGION)
                .build();

        this.awsIam = awsIam;

        this.awsIamRoleArn = this.awsIam.createIamRole();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            // create zip file containing function source code files
            InputStream sourceCodeInputStream = getClass().getClassLoader().getResourceAsStream(function.getSourceCodePath());
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
}
