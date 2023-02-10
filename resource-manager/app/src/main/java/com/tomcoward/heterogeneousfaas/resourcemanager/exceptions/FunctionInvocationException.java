package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

public class FunctionInvocationException extends IntegrationException {
    private int httpErrorCode;

    public FunctionInvocationException(String errorMessage, int httpErrorCode) {
        super(errorMessage);

        this.httpErrorCode = httpErrorCode;
    }


    public int getHttpErrorCode() {
        return this.httpErrorCode;
    }
}
