package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import java.util.List;
import java.util.UUID;

public interface IWorkerRepository {
    Worker get(UUID id) throws DBClientException;
    List<Worker> getAll() throws DBClientException;
    void create(Worker worker) throws DBClientException;
}
