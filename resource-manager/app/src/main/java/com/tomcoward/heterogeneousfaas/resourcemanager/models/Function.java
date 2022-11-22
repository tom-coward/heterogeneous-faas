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
    @CqlName("source_code_runtime")
    private SourceCodeRuntime sourceCodeRuntime;
    @CqlName("edge_supported")
    private boolean edgeSupported;
    @CqlName("cloud_aws_supported")
    private boolean cloudAWSSupported;

    public enum SourceCodeRuntime {
        JDK11
    }

    public Function() {}

    public Function(String name, byte[] sourceCode, SourceCodeRuntime sourceCodeRuntime, boolean edgeSupported, boolean cloudAWSSupported) {
        this.name = name;
        this.sourceCode = sourceCode;
        this.sourceCodeRuntime = sourceCodeRuntime;
        this.edgeSupported = edgeSupported;
        this.cloudAWSSupported = cloudAWSSupported;
    }

    public Function(JsonObject jsonObject) throws IOException {
        String name = jsonObject.getString("name");

        this.name = jsonObject.getString("name");
        this.sourceCode = jsonObject.getString("source_code").getBytes();
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

    public SourceCodeRuntime getSourceCodeRuntime() {
        return sourceCodeRuntime;
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
}
