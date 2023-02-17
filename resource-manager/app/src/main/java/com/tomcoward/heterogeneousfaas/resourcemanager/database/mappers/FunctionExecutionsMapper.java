package com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.FunctionExecutionsDao;

@Mapper
public interface FunctionExecutionsMapper {
    @DaoFactory
    FunctionExecutionsDao functionExecutionsDao(@DaoKeyspace CqlIdentifier keyspace);
}