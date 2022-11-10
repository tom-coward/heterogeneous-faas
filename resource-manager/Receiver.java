package resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Receiver {
    private static final int SERVER_PORT = 5001;

    private HttpServer server;

    public Receiver() {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(SERVER_PORT);
            server = HttpServer.create(serverAddress, 0);
        } catch (IOException ex) {
            System.err.println(String.format("There wasn an error starting the HTTP server: {0}", ex.getMessage()));
            System.exit(-1);
        }

        addInvokeFunctionRoute();
    }
    
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
    }


    private void addInvokeFunctionRoute() {
        server.createContext("/invoke", new InvokeFunctionHandler());
    }
}
