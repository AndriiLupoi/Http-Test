package org.lupoi.http_test;/*
    @author user
    @project HTTP_Test
    @class GlobalExceptionHandler
    @version 1.0.0
    @since 07.05.2025 - 12.20
*/

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
