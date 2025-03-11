package org.greedy.ddarahang.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<String> invalidFilterException(InvalidFilterException e) {
        log.error("InvalidFilterException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundTravelCourseDetailException.class)
    public ResponseEntity<String> notFoundTravelCourseDetailException(NotFoundTravelCourseDetailException e) {
        log.error("NotFoundTravelCourseDetailException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidCountryNameException.class)
    public ResponseEntity<String> invalidCountryNameException(InvalidCountryNameException e) {
        log.error("InvalidCountryNameException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MissingIdException.class)
    public ResponseEntity<String> missingIdException(MissingIdException e) {
        log.error("MissingIdException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundCountryException.class)
    public ResponseEntity<String> notFoundCountryException(NotFoundCountryException e) {
        log.error("NotFoundCountryException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotFoundRegionException.class)
    public ResponseEntity<String> notFoundRegionException(NotFoundRegionException e) {
        log.error("NotFoundRegionException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidDateFormat.class)
    public ResponseEntity<String> invalidDateFormat(InvalidDateFormat e) {
        log.error("InvalidDateFormat 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataSyncException.class)
    public ResponseEntity<String> dataSyncException(DataSyncException e) {
        log.error("DataSyncException 발생: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DdarahangException.class)
    public ResponseEntity<String> handleExceptions(DdarahangException e) {
        log.error("DdarahangException 발생: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("프로그램 내 에러가 발생했습니다.");
    }
}
