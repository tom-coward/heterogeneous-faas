package com.tomcoward.heterogeneousfaas.resourcemanager.database;

import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

public interface IDBClient {
    void up() throws DBClientException;
    void down() throws DBClientException;
    ResultSet execute(SimpleStatement statement);
}
