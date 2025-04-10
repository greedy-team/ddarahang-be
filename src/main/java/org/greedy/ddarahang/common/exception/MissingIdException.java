package org.greedy.ddarahang.common.exception;

public class MissingIdException extends DdarahangException {
    public MissingIdException() {
        super(ErrorMessage.MISSING_ID);
    }
}
