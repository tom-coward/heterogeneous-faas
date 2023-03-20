package com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers;

import io.undertow.server.HttpServerExchange;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;

public class HttpHelper {
    public static JsonObject getRequestBody(HttpServerExchange httpExchange, String jsonObjectName) throws IOException {
        httpExchange.startBlocking();
        InputStream requestBody = httpExchange.getInputStream();

        // deserialize json from request body
        JsonReader jsonReader = Json.createReader(requestBody);
        JsonObject jsonObject = jsonReader.readObject();
        requestBody.close();
        jsonReader.close();

        // create json object
        JsonObject jsonRequestBody;
        if (jsonObjectName != null && jsonObjectName != "") {
            jsonRequestBody = jsonObject.getJsonObject(jsonObjectName);
        } else {
            jsonRequestBody = jsonObject;
        }

        return jsonRequestBody;
    }

    public static void sendResponse(HttpServerExchange httpExchange, int responseCode, String response) {
        httpExchange.setStatusCode(responseCode);
        httpExchange.getResponseSender().send(response);

        httpExchange.endExchange();
    }
}
