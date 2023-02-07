package com.tomcoward.heterogeneousfaas.resourcemanager.models;

import javax.json.JsonObject;
import java.io.IOException;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
public class Function {
    @PartitionKey
    @CqlName("name")
    private String name;

    @CqlName("source_code_path")
    private String sourceCodePath;
    @CqlName("source_code_runtime")
    private String sourceCodeRuntime;
    @CqlName("source_code_handler")
    private String sourceCodeHandler;
    @CqlName("edge_supported")
    private boolean edgeSupported;
    @CqlName("cloud_aws_supported")
    private boolean cloudAWSSupported;
    @CqlName("edge_knservice_name")
    private String edgeKnServiceName;
    @CqlName("cloud_aws_arn")
    private String cloudAwsArn;

    public Function() {}

    public Function(JsonObject jsonObject) throws IOException {
        this.name = jsonObject.getString("name");
        this.sourceCodePath = jsonObject.getString("source_code_path");
        this.sourceCodeRuntime = jsonObject.getString("source_code_runtime");
        this.sourceCodeHandler = jsonObject.getString("source_code_handler");
        this.edgeSupported = jsonObject.getBoolean("edge_supported");
        this.cloudAWSSupported = jsonObject.getBoolean("cloud_aws_supported");
    }


    public String getName() {
        return name;
    }

    public String getSourceCodePath() {
        return sourceCodePath;
    }

    public String getSourceCodeRuntime() {
        return sourceCodeRuntime;
    }

    public String getSourceCodeHandler() {
        return sourceCodeHandler;
    }

    public boolean isEdgeSupported() {
        return edgeSupported;
    }

    public boolean isCloudAWSSupported() {
        return cloudAWSSupported;
    }

    public String getEdgeKnServiceName() {
        return edgeKnServiceName;
    }

    public String getCloudAwsArn() {
        return cloudAwsArn;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSourceCodePath(String sourceCodePath) {
        this.sourceCodePath = sourceCodePath;
    }

    public void setSourceCodeRuntime(String sourceCodeRuntime) {
        this.sourceCodeRuntime = sourceCodeRuntime;
    }

    public void setSourceCodeHandler(String sourceCodeHandler) {
        this.sourceCodeHandler = sourceCodeHandler;
    }

    public void setEdgeSupported(boolean edgeSupported) {
        this.edgeSupported = edgeSupported;
    }

    public void setCloudAWSSupported(boolean cloudAWSSupported) {
        this.cloudAWSSupported = cloudAWSSupported;
    }

    public void setEdgeKnServiceName(String edgeKnServiceName) {
        this.edgeKnServiceName = edgeKnServiceName;
    }

    public void setCloudAwsArn(String cloudAwsArn) {
        this.cloudAwsArn = cloudAwsArn;
    }
}
