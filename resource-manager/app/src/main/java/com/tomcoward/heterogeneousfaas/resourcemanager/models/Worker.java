package com.tomcoward.heterogeneousfaas.resourcemanager.models;

import java.util.UUID;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
public class Worker {
    @PartitionKey
    @CqlName("id")
    private UUID id;

    @CqlName("host")
    private Host host;
    @CqlName("status")
    private Status status;

    public enum Host {
        EDGE_KUBERNETES("KUBERNETES"),
        CLOUD_AWS("AWS");

        private final String name;

        Host(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Status {
        AVAILABLE, BUSY, OFFLINE
    }

    public Worker() {}

    public Worker(UUID id, Host host, Status status) {
        this.id = id;
        this.host = host;
        this.status = status;
    }


    public UUID getId() {
        return id;
    }

    public Host getHost() {
        return host;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status.equals(Status.AVAILABLE);
    }
}
