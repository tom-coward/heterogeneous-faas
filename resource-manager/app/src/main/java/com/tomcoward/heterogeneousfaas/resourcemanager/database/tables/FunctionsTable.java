package com.tomcoward.heterogeneousfaas.resourcemanager.database.tables;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.CassandraClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class FunctionsTable implements IDBTable {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final String TABLE_NAME = String.format("%s.functions", CassandraClient.KEYSPACE_NAME);

    private final IDBClient db;

    public FunctionsTable(IDBClient db) {
        this.db = db;
    }


    public void up() throws DBClientException {
        try {
            SimpleStatement statement = createTable(TABLE_NAME)
                    .ifNotExists()
                    .withPartitionKey("name", DataTypes.ASCII)
                    .withColumn("source_code", DataTypes.BLOB)
                    .withColumn("source_code_runtime", DataTypes.ASCII)
                    .withColumn("edge_supported", DataTypes.BOOLEAN)
                    .withColumn("cloud_aws_supported", DataTypes.BOOLEAN)
                    .build();

            db.execute(statement);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error creating %s table", TABLE_NAME), ex);
            throw new DBClientException("There was a problem setting up the database");
        }
    }

    public void down() throws DBClientException {
        try {
            SimpleStatement statement = dropTable(TABLE_NAME)
                .ifExists()
                .build();

            db.execute(statement);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error dropping %s table", TABLE_NAME), ex);
            throw new DBClientException("There was a problem setting up the database");
        }
    }
}
