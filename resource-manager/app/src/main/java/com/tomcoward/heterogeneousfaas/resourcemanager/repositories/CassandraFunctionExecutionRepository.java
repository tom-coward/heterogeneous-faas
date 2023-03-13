package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionExecutionsDao;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.FunctionExecution;

public class CassandraFunctionExecutionRepository implements IFunctionExecutionRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;

    private final FunctionExecutionsDao functionExecutionsDao;

    public CassandraFunctionExecutionRepository(IDBClient db) {
        this.db = db;

        this.functionExecutionsDao = db.getFunctionExecutionsDao();
    }


    public FunctionExecution get(UUID id) throws DBClientException {
        try {
            return functionExecutionsDao.get(id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting FunctionExecution from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting the \"%s\" function execution from the database", id.toString()));
        }
    }

    public List<FunctionExecution> getByFunction(String functionName) throws DBClientException {
        try {
            return functionExecutionsDao.getByFunctionName(functionName).all();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error getting FunctionExecutions for function \"%s\" from Cassandra", functionName), ex);
            throw new DBClientException("There was a problem getting function execution history from the database");
        }
    }

    public List<FunctionExecution> getByWorker(String worker) throws DBClientException {
        try {
            return functionExecutionsDao.getByWorker(worker).all();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error getting FunctionExecutions for worker %s from Cassandra", worker), ex);
            throw new DBClientException("There was a problem getting function execution history from the database");
        }
    }

    public void create(FunctionExecution functionExecution) throws DBClientException {
        try {
            functionExecutionsDao.create(functionExecution);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding FunctionExecution to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding the \"%s\" function execution to the database", functionExecution.getId().toString()));
        }
    }
}
