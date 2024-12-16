package com.kiryukhin.mental_health.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(AuthenticationFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(RegistrationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRegistrationFailedException(RegistrationFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Registration failed: " + ex.getMessage());
    }

    @ExceptionHandler(TokenFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRegistrationFailedException(TokenFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token invalid failed: " + ex.getMessage());
    }

    @ExceptionHandler(UserIsBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(UserIsBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User is blocked: " + ex.getMessage());
    }

    @ExceptionHandler(UserIsNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(UserIsNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User is no verified: " + ex.getMessage());
    }
}
