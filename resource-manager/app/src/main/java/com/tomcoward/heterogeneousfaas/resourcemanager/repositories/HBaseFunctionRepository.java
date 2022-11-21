package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class HBaseFunctionRepository implements IFunctionRepository {
    private static final String HBASE_SERVER_ADDR = "127.0.0.1";
    private static final int HBASE_SERVER_PORT = 9042;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Connection connection;

    public HBaseFunctionRepository() throws IOException {
        initialiseDbConnection();
    }


    public void get() {
        //
    }

    public void create() {
        //
    }


    private void initialiseDbConnection() throws IOException {
        Configuration config = getDbConfig();

        try {
            Connection connection = ConnectionFactory.createConnection(config);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to HBase DB", ex);
            throw ex;
        }
    }

    private Configuration getDbConfig() {
        Configuration config = HBaseConfiguration.create();

        config.addResource();
    }
}
