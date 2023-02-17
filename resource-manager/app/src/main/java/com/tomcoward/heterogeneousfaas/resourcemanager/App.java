package com.tomcoward.heterogeneousfaas.resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.*;

public class App {
    private static final int SERVER_PORT = 5001;

    private final HttpServer server;

    private final IDBClient db;

    private final AWSIAM awsIam;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;

    private final IFunctionRepository functionsRepo;
    private final IWorkerRepository workersRepo;
    private final IFunctionExecutionRepository functionExecutionsRepo;

    public App() throws Exception {
        // setup db client instance
        db = new CassandraClient();

        // setup AWS ECR/Lambda & k8s clients
        awsIam = new AWSIAM();
        awsLambda = new AWSLambda(awsIam);
        kubernetes = new Kubernetes();

        // initialise repos
        functionsRepo = new CassandraFunctionRepository(db);
        workersRepo = new CassandraWorkerRepository(db);
        functionExecutionsRepo = new CassandraFunctionExecutionRepository(db);

        // initialise http server
        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_PORT);
        server = HttpServer.create(serverAddress, 0);

        // define http server routes
        addCreateFunctionRoute();
        addInvokeFunctionRoute();
        addSetCredentialsRoute();

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
            System.err.println(String.format("ERROR: %s", ex.getMessage()));
            System.exit(-1);
        }
    }


    private void addCreateFunctionRoute() {
        server.createContext("/function", new CreateFunctionHandler(functionsRepo, workersRepo, functionExecutionsRepo, awsLambda, kubernetes));
    }

    private void addInvokeFunctionRoute() {
        server.createContext("/function/invoke", new InvokeFunctionHandler(functionsRepo, workersRepo, functionExecutionsRepo, awsLambda, kubernetes));
    }

    private void addSetCredentialsRoute() {
        server.createContext("/credentials", new SetCredentialsHandler());
    }
}
