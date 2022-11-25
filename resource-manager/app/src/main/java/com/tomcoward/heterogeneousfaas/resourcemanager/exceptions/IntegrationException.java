package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class IntegrationException extends Exception {
    public IntegrationException(String errorMessage) {
        super(errorMessage);
    }
}
