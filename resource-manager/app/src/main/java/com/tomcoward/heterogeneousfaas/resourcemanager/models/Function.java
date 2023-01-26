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
    @CqlName("source_code_handler")
    private String sourceCodeHandler;
    /*
    Accepted source code runtimes (match AWS Lambda runtime IDs):
    - nodejs18.x
    - python3.9
    - java11
    - go1.x
     */
    @CqlName("source_code_runtime")
    private String sourceCodeRuntime;
    @CqlName("edge_supported")
    private boolean edgeSupported;
    @CqlName("cloud_aws_supported")
    private boolean cloudAWSSupported;
    @CqlName("cloud_aws_arn")
    private String cloudAwsArn;

    public Function() {}

    public Function(String name, String sourceCodePath, String sourceCodeHandler, String sourceCodeRuntime, boolean edgeSupported, boolean cloudAWSSupported) {
        this.name = name;
        this.sourceCodePath = sourceCodePath;
        this.sourceCodeHandler = sourceCodeHandler;
        this.sourceCodeRuntime = sourceCodeRuntime;
        this.edgeSupported = edgeSupported;
        this.cloudAWSSupported = cloudAWSSupported;
    }

    public Function(JsonObject jsonObject) throws IOException {
        this.name = jsonObject.getString("name");
        this.sourceCodePath = jsonObject.getString("source_code_path");
        this.sourceCodeHandler = jsonObject.getString("source_code_handler");
        this.sourceCodeRuntime = jsonObject.getString("source_code_runtime");
        this.edgeSupported = jsonObject.getBoolean("edge_supported");
        this.cloudAWSSupported = jsonObject.getBoolean("cloud_aws_supported");
    }


    public String getName() {
        return name;
    }

    public String getSourceCodePath() {
        return sourceCodePath;
    }

    public String getSourceCodeHandler() {
        return sourceCodeHandler;
    }

    public String getSourceCodeRuntime() {
        return sourceCodeRuntime;
    }

    public String getCloudAwsArn() {
        return cloudAwsArn;
    }

    public boolean isEdgeSupported() {
        return edgeSupported;
    }

    public boolean isCloudAWSSupported() {
        return cloudAWSSupported;
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

    public void setEdgeSupported(boolean edgeSupported) {
        this.edgeSupported = edgeSupported;
    }

    public void setCloudAWSSupported(boolean cloudAWSSupported) {
        this.cloudAWSSupported = cloudAWSSupported;
    }

    public void setCloudAwsArn(String cloudAwsArn) {
        this.cloudAwsArn = cloudAwsArn;
    }
}
