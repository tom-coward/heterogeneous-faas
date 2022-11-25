package com.tomcoward.heterogeneousfaas.resourcemanager.exceptions;

import java.io.IOException;

public class FunctionException extends IOException {
    public FunctionException(String errorMessage) {
        super(errorMessage);
    }
}
