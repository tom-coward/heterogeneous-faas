package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraFunctionRepository;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class CreateFunctionHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;
    private final IFunctionRepository functionsRepo;

    public CreateFunctionHandler(IDBClient db) {
        this.db = db;
        this.functionsRepo = new CassandraFunctionRepository(db);
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

            Function function = new Function(functionObject);

            functionsRepo.create(function);
        } catch (DBClientException ex) {
            // TODO: return error to client
            return;
        } catch (Exception ex) {
            // TODO: return error to client
            return;
        }
    }
}
