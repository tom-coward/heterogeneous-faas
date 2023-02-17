package com.tomcoward.heterogeneousfaas.resourcemanager.database;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.InvalidKeyspaceException;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionsDao;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.WorkersDao;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.FunctionsMapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.FunctionsMapperBuilder;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.WorkersMapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers.WorkersMapperBuilder;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.tables.FunctionTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.tables.WorkerTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

public class CassandraClient implements IDBClient {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public final static String KEYSPACE_NAME = "heterogeneous_faas";

    private final CqlSession cqlSession;

    private final FunctionTable functionsTable;
    private final WorkerTable workersTable;

    private final FunctionsDao functionsDao;
    private final WorkersDao workersDao;

    public CassandraClient() throws DBClientException {
        this.cqlSession = initialise();

        this.functionsTable = new FunctionTable(this);
        this.workersTable = new WorkerTable(this);

        up();

        FunctionsMapper functionsMapper = new FunctionsMapperBuilder(this.cqlSession).build();
        this.functionsDao = functionsMapper.functionsDao(CqlIdentifier.fromCql(KEYSPACE_NAME));
        WorkersMapper workersMapper = new WorkersMapperBuilder(this.cqlSession).build();
        this.workersDao = workersMapper.workersDao(CqlIdentifier.fromCql(KEYSPACE_NAME));
    }

    public void up() throws DBClientException {
        functionsTable.up();
        workersTable.up();
    }

    public void down() throws DBClientException {
        dropKeyspace(this.cqlSession);

        functionsTable.down();
        workersTable.down();
    }

    public ResultSet execute(SimpleStatement statement) {
        return this.cqlSession.execute(statement);
    }

    public CqlSession getCqlSession() {
        return this.cqlSession;
    }

    public FunctionsDao getFunctionsDao() {
        return this.functionsDao;
    }
    public WorkersDao getWorkersDao() {
        return this.workersDao;
    }


    private CqlSession initialise() throws DBClientException {
        try {
            return CqlSession.builder() // may need more config if/when not running in local dev env
                    .withKeyspace(CqlIdentifier.fromCql(KEYSPACE_NAME))
                    .build();
        } catch (InvalidKeyspaceException ex) {
            // keyspace doesn't yet exist, so: create session without defining keyspace, create keyspace
            CqlSession session = CqlSession.builder().build();
            createKeyspace(session);
            session.close();

            // ... and then try again
            return initialise();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to Cassandra DB", ex);
            throw new DBClientException("There was an error connecting to the database");
        }
    }

    private void createKeyspace(CqlSession session) {
        SimpleStatement statement = com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace(KEYSPACE_NAME)
                .ifNotExists()
                .withSimpleStrategy(1)
                .build();

        session.execute(statement);
    }

    private void dropKeyspace(CqlSession session) {
        SimpleStatement statement = com.datastax.oss.driver.api.querybuilder.SchemaBuilder.dropKeyspace(KEYSPACE_NAME)
                .ifExists()
                .build();

        session.execute(statement);
    }
}