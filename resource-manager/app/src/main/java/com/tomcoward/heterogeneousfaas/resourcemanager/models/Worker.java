package com.tomcoward.heterogeneousfaas.resourcemanager.models;

import java.io.IOException;
import java.util.UUID;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import javax.json.JsonObject;

@Entity
public class Worker {
    @PartitionKey
    @CqlName("id")
    private UUID id;

    // "AWS" or "KUBERNETES"
    @CqlName("host")
    private String host;
    @CqlName("is_active")
    private boolean isActive;


    public Worker() {}

    public Worker(UUID id, String host, boolean isActive) {
        this.id = id;
        this.host = host;
        this.isActive = isActive;
    }

    public Worker(String host, boolean isActive) {
        this.id = UUID.randomUUID();
        this.host = host;
        this.isActive = isActive;
    }

    public Worker(JsonObject jsonObject) throws IOException {
        this.id = UUID.randomUUID();
        this.host = jsonObject.getString("host");
        this.isActive = jsonObject.getBoolean("is_active");
    }


    public UUID getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }


    public class HOST {
        public static final String AWS = "AWS";
        public static final String KUBERNETES = "KUBERNETES";
    }
}