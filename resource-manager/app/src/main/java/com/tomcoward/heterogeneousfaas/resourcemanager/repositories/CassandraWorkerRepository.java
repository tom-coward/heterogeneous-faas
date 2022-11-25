package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.WorkersTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class CassandraWorkerRepository implements IWorkerRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;

    public CassandraWorkerRepository(IDBClient db) {
        this.db = db;
    }


    public Worker get(UUID id) throws DBClientException {
        try {
            SimpleStatement statement = selectFrom(WorkersTable.TABLE_NAME)
                    .all()
                    .whereColumn("id").isEqualTo(literal(id))
                    .build();

            ResultSet result = db.execute(statement);
            return new Worker();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting worker from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting worker with ID %s from the database", id));
        }
    }

    public void create(Worker worker) throws DBClientException {
        try {
            SimpleStatement statement = insertInto(WorkersTable.TABLE_NAME)
                    .value("id", literal(worker.getId()))
                    .value("host", literal(worker.getHost().toString()))
                    .value("status", literal(worker.getStatus().toString()))
                    .build();

            db.execute(statement);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding worker to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding worker with ID %s to the database", worker.getId()));
        }
    }
}
