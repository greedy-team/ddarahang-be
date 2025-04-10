package org.greedy.ddarahang.common.exception;

public class InvalidDateFormatException extends DdarahangException {
    public InvalidDateFormatException() {
        super(ErrorMessage.INVALID_DATE_FORMAT);
    }
}
