package org.greedy.ddarahang.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> invalidFilterException(InvalidDataException e) {
        log.error("InvalidFilterException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<String> notFoundTravelCourseDetailException(NotFoundDataException e) {
        log.error("NotFoundTravelCourseDetailException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> invalidCountryNameException(InvalidDataException e) {
        log.error("InvalidCountryNameException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> missingIdException(InvalidDataException e) {
        log.error("MissingIdException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<String> notFoundCountryException(NotFoundDataException e) {
        log.error("NotFoundCountryException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<String> notFoundRegionException(NotFoundDataException e) {
        log.error("NotFoundRegionException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> invalidDateFormat(InvalidDataException e) {
        log.error("InvalidDateFormat 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<String> dataSyncException(DataSyncException e) {
        log.error("DataSyncException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
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
