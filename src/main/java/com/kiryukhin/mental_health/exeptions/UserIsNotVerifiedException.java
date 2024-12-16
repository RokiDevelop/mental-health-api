package com.kiryukhin.mental_health.exeptions;

public class UserIsNotVerifiedException extends RuntimeException {
    public UserIsNotVerifiedException(String message) {
        super(message);
    }
}