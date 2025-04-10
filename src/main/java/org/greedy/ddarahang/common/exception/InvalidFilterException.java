package org.greedy.ddarahang.common.exception;

public class InvalidFilterException extends DdarahangException {
    public InvalidFilterException() {
        super(ErrorMessage.INVALID_COUNTRY_NAME);
    }
}
