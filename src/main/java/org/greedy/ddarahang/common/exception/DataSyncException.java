package org.greedy.ddarahang.common.exception;

import org.springframework.http.HttpStatus;

public class DataSyncException extends DdarahangException {
    public DataSyncException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
