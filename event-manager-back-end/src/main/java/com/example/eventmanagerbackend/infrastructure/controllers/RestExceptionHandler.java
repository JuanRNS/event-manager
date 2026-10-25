package com.example.eventmanagerbackend.infrastructure.controllers;

import com.example.eventmanagerbackend.infrastructure.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({EmailExistsException.class, UserNameExistsException.class})
    private ResponseEntity<String> conflictException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler({
            EmployeeNotFoundException.class,
            EmployeeTypeNotFoundException.class,
            MaterialNotFoundException.class,
            PartyNotFoundException.class,
            UserNotFoundException.class
    })
    private ResponseEntity<String> notFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(UserNameStringFirstException.class)
    private ResponseEntity<String> userNameStringFirstException(UserNameStringFirstException user) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(user.getMessage());
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    private ResponseEntity<String> tooManyLoginAttemptsException(TooManyLoginAttemptsException e) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }
}
