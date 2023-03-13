package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.FunctionExecution;
import java.util.List;
import java.util.UUID;

public interface IFunctionExecutionRepository {
    FunctionExecution get(UUID id) throws DBClientException;
    List<FunctionExecution> getByFunction(String functionName) throws DBClientException;
    List<FunctionExecution> getByWorker(String worker) throws DBClientException;
    void create(FunctionExecution functionExecution) throws DBClientException;
}