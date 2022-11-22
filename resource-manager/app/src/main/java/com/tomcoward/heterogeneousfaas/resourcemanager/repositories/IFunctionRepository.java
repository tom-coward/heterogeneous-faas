package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;

@Dao
public interface IFunctionRepository {
    @Select
    Function get(String functionName) throws DBClientException;
    @Insert
    void create(Function function) throws DBClientException;
}
