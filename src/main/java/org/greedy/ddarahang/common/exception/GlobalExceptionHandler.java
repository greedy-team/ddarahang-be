package org.greedy.ddarahang.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String processValidationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        return builder.toString();
    }

    @ExceptionHandler(NotFoundTravelCourseDetailException.class)
    public ResponseEntity<String> notFoundTravelCourseDetailException(NotFoundTravelCourseDetailException e) {
        log.error("");
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InvalidCountryNameException.class)
    public ResponseEntity<String> invalidCountryNameException(InvalidCountryNameException e) {
        log.error("");
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DdarahangException.class)
    public ResponseEntity<String> handleExceptions(DdarahangException e) {
        log.error("");
        return ResponseEntity.internalServerError().body("프로그램 내 에러가 발생했습니다.");
    }
}
