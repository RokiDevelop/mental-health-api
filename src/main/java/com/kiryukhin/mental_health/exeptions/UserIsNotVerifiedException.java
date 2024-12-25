package com.kiryukhin.mental_health.exeptions;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class UserIsNotVerifiedException extends InternalAuthenticationServiceException {
    public UserIsNotVerifiedException(String message) {
        super(message);
    }
}