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

    @CqlName("source_code")
    private byte[] sourceCode;
    @CqlName("source_code_handler")
    private String sourceCodeHandler;
    @CqlName("source_code_runtime")
    private SourceCodeRuntime sourceCodeRuntime;
    @CqlName("edge_supported")
    private boolean edgeSupported;
    @CqlName("cloud_aws_supported")
    private boolean cloudAWSSupported;
    @CqlName("cloud_aws_arn")
    private String cloudAwsArn;

    public enum SourceCodeRuntime {
        NODEJS18 ("nodejs18.x"),
        PYTHON39 ("python3.9"),
        JAVA11 ("java11"),
        GO1 ("go1.x");

        private String awsRuntime;

        SourceCodeRuntime(String awsRuntime) {
            this.awsRuntime = awsRuntime;
        }

        public String getAwsRuntime() {
            return this.awsRuntime;
        }
    }

    public Function() {}

    public Function(String name, byte[] sourceCode, String sourceCodeHandler, SourceCodeRuntime sourceCodeRuntime, boolean edgeSupported, boolean cloudAWSSupported) {
        this.name = name;
        this.sourceCode = sourceCode;
        this.sourceCodeHandler = sourceCodeHandler;
        this.sourceCodeRuntime = sourceCodeRuntime;
        this.edgeSupported = edgeSupported;
        this.cloudAWSSupported = cloudAWSSupported;
    }

    public Function(JsonObject jsonObject) throws IOException {
        this.name = jsonObject.getString("name");
        this.sourceCode = jsonObject.getString("source_code").getBytes();
        this.sourceCodeHandler = jsonObject.getString("source_code_handler");
        this.sourceCodeRuntime = SourceCodeRuntime.valueOf(jsonObject.getString("source_code_runtime"));
        this.edgeSupported = jsonObject.getBoolean("edge_supported");
        this.cloudAWSSupported = jsonObject.getBoolean("cloud_aws_supported");
    }


    public String getName() {
        return name;
    }

    public byte[] getSourceCode() {
        return sourceCode;
    }

    public String getSourceCodeHandler() {
        return sourceCodeHandler;
    }

    public SourceCodeRuntime getSourceCodeRuntime() {
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

    public void setSourceCode(byte[] sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void setSourceCodeRuntime(SourceCodeRuntime sourceCodeRuntime) {
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
