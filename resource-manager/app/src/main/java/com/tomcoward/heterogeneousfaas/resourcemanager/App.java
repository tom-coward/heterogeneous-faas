package com.tomcoward.heterogeneousfaas.resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraWorkerRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IWorkerRepository;

public class App {
    private static final int SERVER_PORT = 5001;

    private final HttpServer server;

    private final IDBClient db;

    private final AWSLambda awsLambda;

    private final IFunctionRepository functionsRepo;
    private final IWorkerRepository workersRepo;

    public App() throws Exception {
        // setup db client instance
        db = new CassandraClient();

        // setup AWS Lambda client
        awsLambda = new AWSLambda();

        // initialise repos
        functionsRepo = new CassandraFunctionRepository(db);
        workersRepo = new CassandraWorkerRepository(db);

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
        server.createContext("/invoke", new InvokeFunctionHandler(functionsRepo, awsLambda));
    }

    private void addCreateFunctionRoute() {
        server.createContext("/create", new CreateFunctionHandler(functionsRepo, awsLambda));
    }
}
