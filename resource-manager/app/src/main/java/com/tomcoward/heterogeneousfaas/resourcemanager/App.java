package com.tomcoward.heterogeneousfaas.resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.FunctionsMapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.InvalidFunctionException;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

public class App {
    private static final int SERVER_PORT = 5001;

    private final HttpServer server;

    private final IDBClient db;


    public App() throws Exception {
        // setup db client instance
        db = new CassandraClient();

        // initialise repos


        // setup http server
        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_PORT);
        server = HttpServer.create(serverAddress, 0);

        // add http server routes
        addInvokeFunctionRoute();
        addCreateFunctionRoute();

        // startup http server (will block thread)
        System.out.println(String.format("Resource Manager server started on port %d", SERVER_PORT));
        server.start();
    }
    
    public static void main(String[] args) {
        try {
            App app = new App();
        } catch (IOException ex) {
            System.err.println(String.format("There was an error starting the HTTP server: %s", ex.getMessage()));
            System.exit(-1);
        } catch (Exception ex) {
            System.err.println(String.format("There was an error: %s", ex.getMessage()));
            System.exit(-1);
        }
    }


    private void addInvokeFunctionRoute() {
        server.createContext("/invoke", new InvokeFunctionHandler(db));
    }

    private void addCreateFunctionRoute() {
        server.createContext("/create", new CreateFunctionHandler(db));
    }
}
