package resourcemanager.exceptions;

import java.io.IOException;

public class InvalidFunctionException extends IOException {
    public InvalidFunctionException(String errorMessage) {
        super(errorMessage);
    }
}
