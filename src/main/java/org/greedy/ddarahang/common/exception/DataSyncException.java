package org.greedy.ddarahang.common.exception;

public class DataSyncException extends DdarahangException {
    public DataSyncException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
