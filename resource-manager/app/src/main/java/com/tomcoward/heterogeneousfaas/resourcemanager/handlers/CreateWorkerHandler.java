package com.tomcoward.heterogeneousfaas.resourcemanager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.handlers.helpers.HttpHelper;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IWorkerRepository;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateWorkerHandler implements com.sun.net.httpserver.HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Gson gson = new Gson();

    private final IWorkerRepository workersRepo;

    public CreateWorkerHandler(IWorkerRepository workersRepo) {
        this.workersRepo = workersRepo;
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            JsonObject workerObject = HttpHelper.getRequestBody(exchange, "worker");

            Worker worker = createWorker(workerObject);

            LOGGER.log(Level.INFO, String.format("CreateWorkerHandler worker created: \"%s\"", worker.getId().toString()));

            String response = gson.toJson(worker);
            HttpHelper.sendResponse(exchange, 200, response);
        } catch (DBClientException ex) {
            // return error to client
            String response = "There was an issue saving your worker";
            HttpHelper.sendResponse(exchange, 500, response);
        } catch (IOException ex) {
            // return error to client
            LOGGER.log(Level.SEVERE, "Error creating worker", ex);
            String response = "The worker object was invalid";
            HttpHelper.sendResponse(exchange, 400, response);
        } catch (Exception ex) {
            // return error to client
            LOGGER.log(Level.SEVERE, "Error creating worker", ex);
            String response = "There was an issue creating your worker";
            HttpHelper.sendResponse(exchange, 500, response);
        }
    }


    private Worker createWorker(JsonObject workerObject) throws IOException, DBClientException {
        Worker worker = new Worker(workerObject);

        workersRepo.create(worker);

        return worker;
    }
}
