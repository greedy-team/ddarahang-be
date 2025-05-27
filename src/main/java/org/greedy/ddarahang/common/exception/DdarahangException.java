package org.greedy.ddarahang.common.exception;

public class DdarahangException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public DdarahangException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
