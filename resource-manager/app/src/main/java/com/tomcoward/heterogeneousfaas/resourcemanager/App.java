package com.tomcoward.heterogeneousfaas.resourcemanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSECR;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.AWSLambda;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.Kubernetes;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.CassandraFunctionRepository;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

public class App {
    private static final int SERVER_PORT = 5001;

    private final HttpServer server;

    private final IDBClient db;

    private final AWSECR awsEcr;
    private final AWSLambda awsLambda;
    private final Kubernetes kubernetes;

    private final IFunctionRepository functionsRepo;

    public App() throws Exception {
        // setup db client instance
        db = new CassandraClient();

        // setup AWS ECR/Lambda & k8s clients
        awsEcr = new AWSECR();
        awsLambda = new AWSLambda();
        kubernetes = new Kubernetes();

        // initialise repos
        functionsRepo = new CassandraFunctionRepository(db);

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
        server.createContext("/function", new CreateFunctionHandler(functionsRepo, awsEcr, awsLambda, kubernetes));
    }

    private void addInvokeFunctionRoute() {
        server.createContext("/function/invoke", new InvokeFunctionHandler(functionsRepo, awsLambda, kubernetes));
    }

    private void addSetCredentialsRoute() {
        server.createContext("/credentials", new SetCredentialsHandler());
    }
}
