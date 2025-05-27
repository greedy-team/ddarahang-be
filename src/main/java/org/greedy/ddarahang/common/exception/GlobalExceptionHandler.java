package org.greedy.ddarahang.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.greedy.ddarahang.api.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidDataException e) {
        return createErrorResponse(e.getErrorMessage(), e);
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundDataException(NotFoundDataException e) {
        return createErrorResponse(e.getErrorMessage(), e);
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<ErrorResponse> handleDataSyncException(DataSyncException e) {
        return createErrorResponse(e.getErrorMessage(), e);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        return createErrorResponse(ErrorMessage.VALIDATION_BIND_ERROR, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return createErrorResponse(ErrorMessage.VALIDATION_ARGUMENT_NOT_VALID, e);
    }

    @ExceptionHandler(DdarahangException.class)
    public ResponseEntity<ErrorResponse> handleDdarahangException(DdarahangException e) {
        return createErrorResponse(e.getErrorMessage(), e);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorMessage error, Exception exception) {
        log.error("{} 발생: {}", error.name(), exception.getMessage(), exception);

        ErrorResponse response = new ErrorResponse(error.getHttpStatus(), error.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }
}
