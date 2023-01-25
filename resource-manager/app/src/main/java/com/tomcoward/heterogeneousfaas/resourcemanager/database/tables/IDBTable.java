package com.tomcoward.heterogeneousfaas.resourcemanager.database.tables;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;

public interface IDBTable {
    void up() throws DBClientException;
    void down() throws DBClientException;
}
