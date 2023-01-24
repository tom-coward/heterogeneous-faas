package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.FunctionsTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionsDAO;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.FunctionsMapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.FunctionsMapperBuilder;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class CassandraFunctionRepository implements IFunctionRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;
    private final FunctionsDAO functionsDao;

    public CassandraFunctionRepository(IDBClient db) {
        this.db = db;

        FunctionsMapper functionsMapper = new FunctionsMapperBuilder(db.getCqlSession()).build();
        this.functionsDao = functionsMapper.functionsDao(CqlIdentifier.fromCql("functions"));
    }


    public Function get(String functionName) throws DBClientException {
        try {
            return functionsDao.get(functionName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting function from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting the \"%s\" function from the database", functionName));
        }
    }

    public void create(Function function) throws DBClientException {
        try {
            functionsDao.create(function);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding function to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding the \"%s\" function to the database", function.getName()));
        }
    }
}
