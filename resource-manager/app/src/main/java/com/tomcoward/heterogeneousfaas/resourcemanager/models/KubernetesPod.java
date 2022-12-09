package com.tomcoward.heterogeneousfaas.resourcemanager.models;

import io.kubernetes.client.openapi.models.V1Pod;
import java.util.UUID;

public class KubernetesPod extends Worker {
    private static final Host HOST = Host.EDGE_KUBERNETES;

    private V1Pod pod;

    public KubernetesPod(UUID id, Status status, V1Pod pod) {
        super(id, HOST, status);

        this.pod = pod;
    }


    public V1Pod getPod() {
        return pod;
    }
}
