package com.kiryukhin.mental_health.exeptions;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequestException(BadRequestException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Bad Request Exception");
        problemDetail.setProperty("error", "BadRequestException");
        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyAssignedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleRegistrationFailedException(UserAlreadyAssignedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("User Already Assigned Exception");
        problemDetail.setProperty("error", "UserAlreadyAssignedException");
        return problemDetail;
    }

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleUserNotFound(UsernameNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
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

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleObjectNotFound(ObjectNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Object Not Found");
        problemDetail.setProperty("error", "ObjectNotFoundException");
        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Not found");
        problemDetail.setProperty("error", "EntityNotFoundException");
        return problemDetail;
    }
}
