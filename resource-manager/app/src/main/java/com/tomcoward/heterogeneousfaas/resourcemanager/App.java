package com.tomcoward.heterogeneousfaas.resourcemanager;

import java.io.IOException;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.integrations.*;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.*;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;

public class App {
    private static final int SERVER_PORT = 5001;

    private final Undertow server;

    private final IDBClient db;

    private final AWSIAM awsIam;
    private final AWSLambda awsLambda;
    private final AWSECR awsEcr;
    private final Kubernetes kubernetes;
    private final LearningManager learningManager;
    private final Docker docker;

    private final IFunctionRepository functionsRepo;
    private final IFunctionExecutionRepository functionExecutionsRepo;

    public App() throws Exception {
        // setup db client instance
        db = new CassandraClient();

        // setup AWS ECR/Lambda & k8s clients
        awsIam = new AWSIAM();
        awsLambda = new AWSLambda(awsIam);
        awsEcr = new AWSECR();
        kubernetes = new Kubernetes();
        learningManager = new LearningManager();
        docker = new Docker(awsEcr);

        // initialise repos
        functionsRepo = new CassandraFunctionRepository(db);
        functionExecutionsRepo = new CassandraFunctionExecutionRepository(db);

        // define http server handlers
        CreateFunctionHandler createFunctionHandler = new CreateFunctionHandler(functionsRepo, functionExecutionsRepo, awsLambda, kubernetes, learningManager, docker);
        HttpHandler createFunctionRequestHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) {
                // Handle request asynchronously in a new thread if currently blocking IO thread
                if (exchange.isInIoThread()) {
                    exchange.dispatch(createFunctionHandler);
                } else {
                    createFunctionHandler.handleRequest(exchange);
                }
            }
        };

        InvokeFunctionHandler invokeFunctionHandler = new InvokeFunctionHandler(functionsRepo, functionExecutionsRepo, awsLambda, kubernetes, learningManager);
        HttpHandler invokeFunctionRequestHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) {
                // Handle request asynchronously in a new thread if currently blocking IO thread
                if (exchange.isInIoThread()) {
                    exchange.dispatch(invokeFunctionHandler);
                } else {
                    invokeFunctionHandler.handleRequest(exchange);
                }
            }
        };

        SetCredentialsHandler setCredentialsHandler = new SetCredentialsHandler();
        HttpHandler setCredentialsRequestHandler = new HttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) {
                // Handle request asynchronously in a new thread if currently blocking IO thread
                if (exchange.isInIoThread()) {
                    exchange.dispatch(setCredentialsHandler);
                } else {
                    setCredentialsHandler.handleRequest(exchange);
                }
            }
        };

        PathHandler pathHandler = Handlers.path()
                .addExactPath("/function", createFunctionRequestHandler)
                .addPrefixPath("/function/invoke", invokeFunctionRequestHandler)
                .addExactPath("/credentials", setCredentialsRequestHandler);

        // initialise http server
        server = Undertow.builder()
                .addHttpListener(SERVER_PORT, "localhost")
                .setHandler(pathHandler)
                .build();

        // startup http server (runs on its own thread so non-blocking)
        server.start();
        System.out.println(String.format("Resource Manager server started on port %d", SERVER_PORT));
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
}
