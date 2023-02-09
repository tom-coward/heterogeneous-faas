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

    @CqlName("container_registry_uri")
    private String containerRegistryUri;
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
        this.containerRegistryUri = jsonObject.getString("container_registry_uri");
        this.edgeSupported = jsonObject.getBoolean("edge_supported");
        this.cloudAWSSupported = jsonObject.getBoolean("cloud_aws_supported");
    }


    public String getName() {
        return name;
    }

    public String getContainerRegistryUri() {
        return containerRegistryUri;
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

    public void setContainerRegistryUri(String containerRegistryUri) {
        this.containerRegistryUri = containerRegistryUri;
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
