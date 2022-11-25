package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Runtime;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraFunctionRepository;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class CreateFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;
    private final IFunctionRepository functionsRepo;
    private final LambdaClient awsLambda;

    public CreateFunctionHandler(IDBClient db) {
        this.db = db;
        this.functionsRepo = new CassandraFunctionRepository(db);
        this.awsLambda = LambdaClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            InputStream requestBody = exchange.getRequestBody();

            // deserialize json from request body
            JsonReader jsonReader = Json.createReader(requestBody);
            JsonObject jsonObject = jsonReader.readObject();
            requestBody.close();
            jsonReader.close();

            // get function object
            JsonObject functionObject = jsonObject.getJsonObject("function");

            createFunction(functionObject);
        } catch (DBClientException ex) {
            // TODO: return error to client
            return;
        } catch (Exception ex) {
            // TODO: return error to client
            return;
        }
    }


    private void createFunction(JsonObject functionObject) throws IOException, DBClientException {
        Function function = new Function(functionObject);

        // if aws supported, list in AWS Lambda
        if (function.isCloudAWSSupported()) {
            function = createAwsLambdaFunction(function);
        }

        // if edge supported, add to kubernetes

        functionsRepo.create(function);
    }

    private Function createAwsLambdaFunction(Function function) {
        LambdaWaiter waiter = awsLambda.waiter();
        SdkBytes fileToUpload = SdkBytes.fromByteArray(function.getSourceCode());

        FunctionCode code = FunctionCode.builder()
                .zipFile(fileToUpload)
                .build();

        CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                .functionName(function.getName())
                .description("Created by the Lambda Java API")
                .code(code)
                .handler(function.getSourceCodeHandler())
                .runtime(function.getAwsRuntime())
                .role(role)
                .build();

        CreateFunctionResponse functionResponse = awsLambda.createFunction(functionRequest);
        GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                .functionName(function.getName())
                .build();
        WaiterResponse<GetFunctionResponse> waiterResponse = waiter.waitUntilFunctionExists(getFunctionRequest);
        waiterResponse.matched().response().ifPresent(System.out::println);

        function.setCloudAWSARN(functionResponse.functionArn());
        return function;
    }
}
