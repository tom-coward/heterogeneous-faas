package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.WorkersDao;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;

public class CassandraWorkerRepository implements IWorkerRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;

    private final WorkersDao workersDao;

    public CassandraWorkerRepository(IDBClient db) {
        this.db = db;

        this.workersDao = db.getWorkersDao();
    }


    public Worker get(UUID id) throws DBClientException {
        try {
            return workersDao.get(id);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting Worker from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting the \"%s\" worker from the database", id.toString()));
        }
    }

    public List<Worker> getAll() throws DBClientException {
        try {
            return workersDao.getAll().all();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting Workers from Cassandra", ex);
            throw new DBClientException("There was a problem getting workers from the database");
        }
    }

    public void create(Worker worker) throws DBClientException {
        try {
            workersDao.create(worker);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding Worker to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding the \"%s\" worker to the database", worker.getId().toString()));
        }
    }
}
