package com.group3.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.group3.backend.dto.Response;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>(null, ex.getMessage(), false));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed. Errors:");
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String defaultMessage = error.getDefaultMessage();
            errorMessage.append(" - ").append(defaultMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>(null, errorMessage.toString(), false));
    }
    
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Response<?>> handleResourceConflictException(ResourceConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Response<>(null, ex.getMessage(), false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception ex) {
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(null, "An unexpected error occurred: " + ex.getMessage(), false));
    }

}
