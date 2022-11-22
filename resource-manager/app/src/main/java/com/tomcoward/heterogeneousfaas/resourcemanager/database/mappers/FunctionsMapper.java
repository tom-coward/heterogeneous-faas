package com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.repositories.IFunctionRepository;

@Mapper
public interface FunctionsMapper {
    @DaoFactory
    IFunctionRepository functionsDao(@DaoKeyspace CqlIdentifier keyspace);
}