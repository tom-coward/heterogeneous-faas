package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;

public interface IFunctionRepository {
    Function get(String functionName) throws DBClientException;
    void create(Function function) throws DBClientException;
}
