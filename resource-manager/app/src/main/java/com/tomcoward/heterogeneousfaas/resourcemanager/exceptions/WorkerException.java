package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class WorkerException extends Exception {
    public WorkerException(String errorMessage) {
        super(errorMessage);
    }
}
