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
    public ResponseEntity<ErrorResponse> invalidFilterException(InvalidDataException e) {
        ErrorMessage error = e.getErrorMessage();
        log.error("{} 발생: {}", error.name(), e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(error.getHttpStatus(), error.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<ErrorResponse> notFoundTravelCourseDetailException(NotFoundDataException e) {
        ErrorMessage error = e.getErrorMessage();
        log.error("{} 발생: {}", error.name(), e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(error.getHttpStatus(), error.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<ErrorResponse> dataSyncException(DataSyncException e) {
        ErrorMessage error = e.getErrorMessage();
        log.error("{} 발생: {}", error.name(), e.getMessage(), e);

        ErrorResponse response = new ErrorResponse(error.getHttpStatus(), error.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(BindException e) {
        log.error("BindException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DdarahangException.class)
    public ResponseEntity<String> handleExceptions(DdarahangException e) {
        log.error("DdarahangException 발생: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("프로그램 내 에러가 발생했습니다.");
    }
}
