package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import com.tomcoward.heterogeneousfaas.resourcemanager.migrations.HBaseClient;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.hadoop.hbase.client.Admin;

public class HBaseFunctionRepository implements IFunctionRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Admin db;

    public HBaseFunctionRepository() throws IOException {
        HBaseClient.up();
        db = HBaseClient.initialise();
    }


    public void get() {
        //
    }

    public void create() {
        db.
    }
}
