package com.tomcoward.heterogeneousfaas.resourcemanager.database.daos;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;

import java.util.UUID;

@Dao
public interface WorkersDao {
    @Select
    Worker get(UUID id) throws DBClientException;
    @Select
    PagingIterable<Worker> getAll() throws DBClientException;
    @Insert
    void create(Worker worker) throws DBClientException;
}
