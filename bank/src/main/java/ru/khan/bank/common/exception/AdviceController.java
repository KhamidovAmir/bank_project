package ru.khan.bank.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdviceController {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(Exception ex){
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}
