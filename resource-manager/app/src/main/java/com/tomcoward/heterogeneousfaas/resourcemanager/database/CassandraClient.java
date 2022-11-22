package com.tomcoward.heterogeneousfaas.resourcemanager.database;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class CassandraClient implements IDBClient {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String CASSANDRA_HOST = "127.0.0.1";
    private final static int CASSANDRA_PORT = 9042;

    private final CqlSession cqlSession;

    private final FunctionsTable functionsTable;

    public CassandraClient() {
        cqlSession = initialise();

        functionsTable = new FunctionsTable(this);
    }

    public void up() throws DBClientException {
        boolean functionsTableExists = tableExists(FunctionsTable.TABLE_NAME);
        if (!functionsTableExists) {
            functionsTable.up();
        }
    }

    public void down() throws DBClientException {
        boolean functionsTableExists = tableExists(FunctionsTable.TABLE_NAME);
        if (functionsTableExists) {
            functionsTable.down();
        }
    }

    public ResultSet execute(SimpleStatement statement) {
        return cqlSession.execute(statement);
    }


    private CqlSession initialise() {
        try {
            return CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(CASSANDRA_HOST, CASSANDRA_PORT))
                    .build();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to Cassandra DB", ex);
        }
    }

    private boolean tableExists(String tableName) {
        Select select = selectFrom("schema_columnfamilies")
                .column("columnfamily_name")
                .whereColumn("keyspace_name").isEqualTo(literal(tableName))
                .countAll();

        SimpleStatement statement = select.build();
        ResultSet result = execute(statement);

        return result.all().size() == 1;
    }
}