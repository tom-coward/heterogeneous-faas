package com.tomcoward.heterogeneousfaas.resourcemanager.database;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class CassandraClient implements IDBClient {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String CASSANDRA_HOST = "172.18.0.2";
    private final static int CASSANDRA_PORT = 9042;

    private final static String KEYSPACE_NAME = "heterogeneousfaas";

    private final CqlSession cqlSession;

    private final FunctionsTable functionsTable;
    private final WorkersTable workersTable;

    public CassandraClient() throws DBClientException {
        cqlSession = initialise();

        functionsTable = new FunctionsTable(this);
        workersTable = new WorkersTable(this);
    }

    public void up() throws DBClientException {
        SimpleStatement statement = createKeyspace(KEYSPACE_NAME)
                .ifNotExists()
                .withSimpleStrategy(1)
                .build();
        execute(statement);

        functionsTable.up();
        workersTable.down();
    }

    public void down() throws DBClientException {
        SimpleStatement statement = dropKeyspace(KEYSPACE_NAME)
                .ifExists()
                .build();
        execute(statement);

        functionsTable.down();
        workersTable.down();
    }

    public ResultSet execute(SimpleStatement statement) {
        return cqlSession.execute(statement);
    }

    public CqlSession getCqlSession() {
        return cqlSession;
    }


    private CqlSession initialise() throws DBClientException {
        try {
            return CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(CASSANDRA_HOST, CASSANDRA_PORT))
                    .build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to Cassandra DB", ex);
            throw new DBClientException("There was an error connecting to the database");
        }
    }
}