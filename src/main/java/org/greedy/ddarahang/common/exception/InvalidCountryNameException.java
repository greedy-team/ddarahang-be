package org.greedy.ddarahang.common.exception;

public class InvalidCountryNameException extends DdarahangException {
    public InvalidCountryNameException() {
        super(ErrorMessage.INVALID_COUNTRY_NAME);
    }
}
