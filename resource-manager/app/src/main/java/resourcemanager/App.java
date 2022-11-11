package resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import resourcemanager.handlers.*;

public class App {
    private static final int SERVER_PORT = 5001;

    private HttpServer server;

    public App() {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(SERVER_PORT);
            server = HttpServer.create(serverAddress, 0);
            addInvokeFunctionRoute();

            System.out.println(String.format("Resource Manager server started on port %d", SERVER_PORT));
            server.start();
        } catch (IOException ex) {
            System.err.println(String.format("There was an error starting the HTTP server: %s", ex.getMessage()));
            System.exit(-1);
        }
    }
    
    public static void main(String[] args) {
        App app = new App();
    }


    private void addInvokeFunctionRoute() {
        server.createContext("/invoke", new InvokeFunctionHandler());
    }
}
