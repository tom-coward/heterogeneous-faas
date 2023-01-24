package com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionsDAO;

@Mapper
public interface FunctionsMapper {
    @DaoFactory
    FunctionsDAO functionsDao(@DaoKeyspace CqlIdentifier keyspace);
}