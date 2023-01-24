package com.tomcoward.heterogeneousfaas.resourcemanager.repositories;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.FunctionsTable;
import com.tomcoward.heterogeneousfaas.resourcemanager.database.IDBClient;
import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.DBClientException;
import com.tomcoward.heterogeneousfaas.resourcemanager.models.Function;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class CassandraFunctionRepository implements IFunctionRepository {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final IDBClient db;

    public CassandraFunctionRepository(IDBClient db) {
        this.db = db;
    }


    public Function get(String functionName) throws DBClientException {
        try {
            SimpleStatement statement = selectFrom(FunctionsTable.TABLE_NAME)
                    .all()
                    .whereColumn("name").isEqualTo(literal(functionName))
                    .build();

            ResultSet result = db.execute(statement);
            return new Function();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error getting function from Cassandra", ex);
            throw new DBClientException(String.format("There was a problem getting the \"%s\" function from the database", functionName));
        }
    }

    public void create(Function function) throws DBClientException {
        try {
            SimpleStatement statement = insertInto(FunctionsTable.TABLE_NAME)
                    .value("name", literal(function.getName()))
                    .value("source_code", typeHint(literal(function.getSourceCode()), DataTypes.BLOB))
                    .value("source_code_handler", literal(function.getSourceCodeHandler()))
                    .value("source_code_runtime", literal(function.getSourceCodeRuntime().toString()))
                    .value("edge_supported", typeHint(literal(function.isEdgeSupported()), DataTypes.BOOLEAN))
                    .value("cloud_aws_supported", typeHint(literal(function.isCloudAWSSupported()), DataTypes.BOOLEAN))
                    .value("cloud_aws_arn", literal(function.getCloudAwsArn()))
                    .build();

            db.execute(statement);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error adding function to Cassandra", ex);
            throw new DBClientException(String.format("There was a problem adding the \"%s\" function to the database", function.getName()));
        }
    }
}
