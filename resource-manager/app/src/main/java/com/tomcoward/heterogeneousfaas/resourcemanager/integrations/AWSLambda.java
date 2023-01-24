package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class AWSLambda implements IWorkerIntegration {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static Region AWS_REGION = Region.EU_WEST_1;

    private final static String S3_KEY_FORMAT = "%s_SourceCode";

    private final LambdaClient lambdaClient;

    public AWSLambda() {
        this.lambdaClient = LambdaClient.builder()
                .region(AWS_REGION)
                .build();
    }


    public Function createFunction(Function function) throws IntegrationException {
        try {
            // create zip file containing function source code
            ByteArrayInputStream sourceCodeInputStream = new ByteArrayInputStream(function.getSourceCode());
            ZipInputStream zipFileInputStream = new ZipInputStream(sourceCodeInputStream);
            SdkBytes sourceCodeZipFile = SdkBytes.fromInputStream(zipFileInputStream);
            zipFileInputStream.close();

            FunctionCode functionCode = FunctionCode.builder()
                    .zipFile(sourceCodeZipFile)
                    .build();

            CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
                    .functionName(function.getName())
                    .runtime(function.getSourceCodeRuntime().toString())
                    .code(functionCode)
                    .handler(function.getSourceCodeHandler())
                    .packageType("Zip")
                    .build();

            String lambdaFunctionArn = lambdaClient.createFunction(createFunctionRequest).functionArn();

            function.setCloudAWSARN(lambdaFunctionArn);

            return function;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error creating AWS Lambda function %s", function.getName()), ex);
            throw new IntegrationException("There was an issue with AWS Lambda");
        }
    }

    public JsonObject invokeFunction(Function function, JsonObject functionPayload) throws IntegrationException {
        try {
            InvokeRequest invokeRequest = InvokeRequest.builder()
                    .functionName(function.getName())
                    .build();

            lambdaClient.invoke(invokeRequest);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error invoking AWS Lambda function", ex);
            throw new IntegrationException("There was an issue invoking the function");
        }
    }
}
