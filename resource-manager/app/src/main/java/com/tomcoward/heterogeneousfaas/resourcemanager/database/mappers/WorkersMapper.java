package com.tomcoward.heterogeneousfaas.resourcemanager.database.mappers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.daos.WorkersDao;

@Mapper
public interface WorkersMapper {
    @DaoFactory
    WorkersDao workersDao(@DaoKeyspace CqlIdentifier keyspace);
}