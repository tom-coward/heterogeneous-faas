import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;

public class InvokeFunctionHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String requestBodyContent = new String(requestBody.readAllBytes());
        requestBody.close();

        System.out.println(requestBodyContent);
    }
}
