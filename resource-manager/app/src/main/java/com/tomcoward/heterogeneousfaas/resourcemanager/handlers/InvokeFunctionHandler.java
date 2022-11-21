package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import resourcemanager.exceptions.InvalidFunctionException;
import java.io.IOException;
import java.io.InputStream;
import javax.json.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.HBaseFunctionRepository;

public class InvokeFunctionHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();

        // deserialize json from request body
        JsonReader jsonReader = Json.createReader(requestBody);
        JsonObject jsonObject = jsonReader.readObject();
        requestBody.close();
        jsonReader.close();

        // get name of function to be invoked
        String functionName = jsonObject.getString("function_name");
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new InvalidFunctionException(String.format("No function with the name %s exists to be invoked", functionName));
        }

        // get payload of function (if any)
        JsonObject functionPayload = jsonObject.getJsonObject("function_payload");

        // TODO: figure out DI of FunctionRepo (is it needed??)
        IFunctionRepository functions = new HBaseFunctionRepository();
        functions.get();

        System.out.println(functionName);
        System.out.println(functionPayload.toString());
    }
}
