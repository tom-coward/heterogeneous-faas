package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class CapacityException extends Exception {
    public CapacityException(String errorMessage) {
        super(errorMessage);
    }
}
