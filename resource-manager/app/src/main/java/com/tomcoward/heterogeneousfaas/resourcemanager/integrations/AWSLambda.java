package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSLambda implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String AWS_IAM_ROLE_ARN = "";

    private final LambdaClient lambdaClient;

    public AWSLambda() {
        this.lambdaClient = LambdaClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            LambdaWaiter waiter = lambdaClient.waiter();
            SdkBytes fileToUpload = SdkBytes.fromByteArray(function.getSourceCode());

            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(function.getName())
                    .description("Heterogeneous FaaS managed function")
                    .code(code)
                    .handler(function.getSourceCodeHandler())
                    .runtime(function.getAwsRuntime())
                    .role(AWS_IAM_ROLE_ARN)
                    .build();

            CreateFunctionResponse functionResponse = lambdaClient.createFunction(functionRequest);
            GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                    .functionName(function.getName())
                    .build();
            WaiterResponse<GetFunctionResponse> waiterResponse = waiter.waitUntilFunctionExists(getFunctionRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);

            function.setCloudAWSARN(functionResponse.functionArn());
            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS Lambda function", ex);
            throw new IntegrationException("There was an issue creating the function on AWS");
        }
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        try {
            String json = functionPayload.toString();

            SdkBytes payload = SdkBytes.fromUtf8String(json);

            InvokeRequest request = InvokeRequest.builder()
                    .functionName(function.getName())
                    .payload(payload)
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            InputStream responseValue = response.payload().asInputStream();
            JsonObject responseJson = Json.createReader(responseValue).readObject();

            return responseJson;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error invoking AWS Lambda function", ex);
            throw new IntegrationException("There was an issue invoking the function");
        }
    }
}
