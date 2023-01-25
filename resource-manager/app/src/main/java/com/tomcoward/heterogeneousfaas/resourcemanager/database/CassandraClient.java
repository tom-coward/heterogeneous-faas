package com.tomcoward.heterogeneousfaas.resourcemanager.database;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.tables.FunctionsTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class CassandraClient implements IDBClient {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public final static String KEYSPACE_NAME = "heterogeneousfaas";

    private final CqlSession cqlSession;

    private final FunctionsTable functionsTable;

    public CassandraClient() throws DBClientException {
        cqlSession = initialise();

        functionsTable = new FunctionsTable(this);

        up();
    }

    public void up() throws DBClientException {
        SimpleStatement statement = createKeyspace(KEYSPACE_NAME)
                .ifNotExists()
                .withSimpleStrategy(1)
                .build();
        execute(statement);

        functionsTable.up();
    }

    public void down() throws DBClientException {
        SimpleStatement statement = dropKeyspace(KEYSPACE_NAME)
                .ifExists()
                .build();
        execute(statement);

        functionsTable.down();
    }

    public ResultSet execute(SimpleStatement statement) {
        return cqlSession.execute(statement);
    }

    public CqlSession getCqlSession() {
        return cqlSession;
    }


    private CqlSession initialise() throws DBClientException {
        try {
            return CqlSession.builder().build(); // may need more config if/when not running in local dev env
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to Cassandra DB", ex);
            throw new DBClientException("There was an error connecting to the database");
        }
    }
}