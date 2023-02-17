package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionsDao;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;

public class CassandraFunctionRepository implements IFunctionRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;

    private final FunctionsDao functionsDao;

    public CassandraFunctionRepository(IDBClient db) {
        this.db = db;

        this.functionsDao = db.getFunctionsDao();
    }


    public Function get(String functionName) throws DBClientException {
        try {
            return functionsDao.get(functionName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting Function from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting the \"%s\" function from the database", functionName));
        }
    }

    public void create(Function function) throws DBClientException {
        try {
            functionsDao.create(function);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding Function to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding the \"%s\" function to the database", function.getName()));
        }
    }
}
