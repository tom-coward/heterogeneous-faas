package com.tomcoward.heterogeneousfaas.resourcemanager.migrations;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HBaseClient {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String HBASE_CONF_PATH = "../hbase/";
    private static final String NAMESPACE_NAME = "heterogeneousfaas";

    public static void up() throws IOException {
        try {
            Admin db = initialise();

            try {
                db.getNamespaceDescriptor(NAMESPACE_NAME);
            } catch (NamespaceNotFoundException ex) {
                createNamespace();
            }

            boolean functionsTableExists = db.tableExists(TableName.valueOf("functions"));
            if (!functionsTableExists) {
                FunctionsTable.up();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error running database migrations", ex);
            throw ex;
        }
    }

    public static void down() throws IOException {
        FunctionsTable.down();
    }


    public static Admin initialise() throws IOException {
        Configuration config = getConfig();

        try {
            Connection connection = ConnectionFactory.createConnection(config);
            Admin admin = connection.getAdmin();

            return admin;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to HBase DB", ex);
            throw ex;
        }
    }

    private static Configuration getConfig() {
        Configuration config = HBaseConfiguration.create();

        config.addResource(new Path(HBASE_CONF_PATH, "hbase-site.xml"));

        return config;
    }

    private static void createNamespace() throws IOException {
        Admin db = initialise();
        db.createNamespace(NamespaceDescriptor.create(NAMESPACE_NAME).build());
    }
}