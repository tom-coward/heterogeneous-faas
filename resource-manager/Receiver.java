package resource-manager;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Receiver {
    private static final int SERVER_PORT = 5001;

    private HttpServer server;

    public Receiver() {
        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_PORT);
        server = HttpServer.create(serverAddress, 0);

        addInvokeFunctionRoute();
    }
    
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
    }


    private void addInvokeFunctionRoute() {
        server.createContext("/invoke", new InvokeFunctionHandler());
    }
}
