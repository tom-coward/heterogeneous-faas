package com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers;

import com.sun.net.httpserver.HttpExchange;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpHelper {
    public static JsonObject getRequestBody(HttpExchange httpExchange, String jsonObjectName) throws IOException {
        InputStream requestBody = httpExchange.getRequestBody();

        // deserialize json from request body
        JsonReader jsonReader = Json.createReader(requestBody);
        JsonObject jsonObject = jsonReader.readObject();
        requestBody.close();
        jsonReader.close();

        // create json object
        JsonObject jsonRequestBody = jsonObject.getJsonObject(jsonObjectName);

        return jsonRequestBody;
    }

    public static void sendResponse(HttpExchange httpExchange, int responseCode, String response) throws IOException {
        httpExchange.sendResponseHeaders(responseCode, response.length());
        OutputStream responseStream = httpExchange.getResponseBody();
        responseStream.write(response.getBytes());
        responseStream.close();
    }
}
