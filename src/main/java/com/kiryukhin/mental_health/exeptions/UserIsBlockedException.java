package com.kiryukhin.mental_health.exeptions;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class UserIsBlockedException extends InternalAuthenticationServiceException {
    public UserIsBlockedException(String message) {
        super(message);
    }
}