package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class CapacityException extends IntegrationException {
    public CapacityException(String errorMessage) {
        super(errorMessage);
    }
}
