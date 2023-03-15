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
    private String sourceCode;
    @CqlName("container_registry_uri")
    private String containerRegistryUri;
    @CqlName("edge_supported")
    private boolean edgeSupported;
    @CqlName("cloud_supported")
    private boolean cloudSupported;
    @CqlName("edge_knservice_uri")
    private String edgeKnServiceUri;
    @CqlName("cloud_aws_arn")
    private String cloudAwsArn;

    public Function() {}

    public Function(JsonObject jsonObject) throws IOException {
        this.name = jsonObject.getString("name");
        this.sourceCode = jsonObject.getString("source_code");
        this.edgeSupported = jsonObject.getBoolean("edge_supported");
        this.cloudSupported = jsonObject.getBoolean("cloud_supported");
    }


    public String getName() {
        return name;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getContainerRegistryUri() {
        return containerRegistryUri;
    }

    public boolean isEdgeSupported() {
        return edgeSupported;
    }

    public boolean isCloudSupported() {
        return cloudSupported;
    }

    public String getEdgeKnServiceUri() {
        return edgeKnServiceUri;
    }

    public String getCloudAwsArn() {
        return cloudAwsArn;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void setContainerRegistryUri(String containerRegistryUri) {
        this.containerRegistryUri = containerRegistryUri;
    }

    public void setEdgeSupported(boolean edgeSupported) {
        this.edgeSupported = edgeSupported;
    }

    public void setCloudSupported(boolean cloudSupported) {
        this.cloudSupported = cloudSupported;
    }

    public void setEdgeKnServiceUri(String edgeKnServiceUri) {
        this.edgeKnServiceUri = edgeKnServiceUri;
    }

    public void setCloudAwsArn(String cloudAwsArn) {
        this.cloudAwsArn = cloudAwsArn;
    }
}
