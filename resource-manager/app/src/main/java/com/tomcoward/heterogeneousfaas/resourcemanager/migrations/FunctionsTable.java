package com.tomcoward.heterogeneousfaas.resourcemanager.migrations;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptorUtils;

import java.io.IOException;

public class FunctionsTable {
    private static final String TABLE_NAME = "functions";

    public static void up() throws IOException {
        Admin db = HBaseClient.initialise();

        TableDescriptor table = TableDescriptorBuilder.newBuilder(TableName.valueOf(TABLE_NAME)).build();
        db.createTable(table);
    }

    public static void down() throws IOException {
        Admin db = HBaseClient.initialise();

        db.deleteTable(TableName.valueOf(TABLE_NAME));
    }
}
