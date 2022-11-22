package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.InvalidFunctionException;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraFunctionRepository;

public class InvokeFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;
    private final IFunctionRepository functionsRepo;

    public InvokeFunctionHandler(IDBClient db) {
        this.db = db;
        this.functionsRepo = new CassandraFunctionRepository(db);
    }


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

        LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionName input: \"%s\"", functionName));
        LOGGER.log(Level.INFO, String.format("InvokeFunctionHandler functionPayload input: \"%s\"", functionPayload.toString()));

        functionsRepo.get(functionName);
    }
}
