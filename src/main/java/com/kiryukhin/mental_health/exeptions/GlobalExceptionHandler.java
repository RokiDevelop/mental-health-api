package com.kiryukhin.mental_health.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Internal Authentication Service Exception");
        problemDetail.setProperty("error", "InternalAuthenticationServiceException");
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleAuthenticationFailedException(AuthenticationFailedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setProperty("error", "AuthenticationFailedException");
        return problemDetail;
    }

    @ExceptionHandler(RegistrationFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleRegistrationFailedException(RegistrationFailedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Registration Failed");
        problemDetail.setProperty("error", "RegistrationFailedException");
        return problemDetail;
    }

    @ExceptionHandler(TokenFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleTokenFailedException(TokenFailedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Token Failed");
        problemDetail.setProperty("error", "TokenFailedException");
        return problemDetail;
    }

    @ExceptionHandler(UserIsBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleUserIsBlockedException(UserIsBlockedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User Is Blocked");
        problemDetail.setProperty("error", "UserIsBlockedException");
        return problemDetail;
    }

    @ExceptionHandler(UserIsNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleUserIsNotVerifiedException(UserIsNotVerifiedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User Is Not Verified");
        problemDetail.setProperty("error", "UserIsNotVerifiedException");
        return problemDetail;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleUserNotFound(UsernameNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("User Not Found");
        problemDetail.setProperty("error", "UsernameNotFoundException");
        return problemDetail;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleAnyRuntimeException(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Error");
        problemDetail.setProperty("error", "RuntimeException");
        return problemDetail;
    }
}
