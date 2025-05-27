package org.greedy.ddarahang.common.exception;

public class InvalidDataException extends DdarahangException {
    public InvalidDataException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
