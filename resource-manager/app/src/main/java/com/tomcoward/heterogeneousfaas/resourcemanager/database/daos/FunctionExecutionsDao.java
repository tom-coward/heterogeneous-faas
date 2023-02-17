package com.tomcoward.heterogeneousfaas.resourcemanager.database.daos;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.tables.FunctionExecutionTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.FunctionExecution;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Worker;
import java.util.UUID;

@Dao
public interface FunctionExecutionsDao {
    @Select
    FunctionExecution get(UUID id) throws DBClientException;

    @Query("SELECT * FROM ${qualifiedTableId} WHERE function_name = :functionName")
    PagingIterable<FunctionExecution> getByFunctionName(String functionName) throws DBClientException;

    @Query("SELECT * FROM ${qualifiedTableId} WHERE worker_id = :workerId")
    PagingIterable<FunctionExecution> getByWorkerId(UUID workerId) throws DBClientException;

    @Insert
    void create(FunctionExecution functionExecution) throws DBClientException;
}
