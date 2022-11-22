package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class DBClientException extends Exception {
    public DBClientException(String errorMessage) {
        super(errorMessage);
    }
}
