package resourcemanager;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import javax.json.*;

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

        System.out.println(functionName);
    }
}
