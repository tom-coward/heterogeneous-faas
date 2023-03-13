package com.tomcoward.heterogeneousfaas.resourcemanager.database.tables;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class FunctionExecutionTable implements IDBTable {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String TABLE_NAME = "function_execution";

    private final IDBClient db;

    public FunctionExecutionTable(IDBClient db) {
        this.db = db;
    }


    public void up() throws DBClientException {
        try {
            // create table
            SimpleStatement createTableStatement = createTable(TABLE_NAME)
                    .ifNotExists()
                    .withPartitionKey("id", DataTypes.UUID)
                    .withColumn("function_name", DataTypes.ASCII)
                    .withColumn("worker_id", DataTypes.UUID)
                    .withColumn("input_size", DataTypes.INT)
                    .withColumn("duration", DataTypes.BIGINT)
                    .withColumn("predicted_duration", DataTypes.BIGINT)
                    .withColumn("is_success", DataTypes.BOOLEAN)
                    .build();

            db.execute(createTableStatement);

            // create function_name column index
            SimpleStatement createFunctionNameIndexStatement = createIndex(String.format("%s_function_name_index", TABLE_NAME))
                    .ifNotExists()
                    .onTable(TABLE_NAME)
                    .andColumn("function_name")
                    .build();

            db.execute(createFunctionNameIndexStatement);

            // create worker_id column index
            SimpleStatement createWorkerIdIndexStatement = createIndex(String.format("%s_worker_id_index", TABLE_NAME))
                    .ifNotExists()
                    .onTable(TABLE_NAME)
                    .andColumn("worker_id")
                    .build();

            db.execute(createWorkerIdIndexStatement);

            // create is_success column index
            SimpleStatement createIsSuccessIndexStatement = createIndex(String.format("%s_is_success_index", TABLE_NAME))
                    .ifNotExists()
                    .onTable(TABLE_NAME)
                    .andColumn("is_success")
                    .build();

            db.execute(createIsSuccessIndexStatement);
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
