package com.kiryukhin.mental_health.exeptions;

public class UserIsBlockedException extends RuntimeException {
    public UserIsBlockedException(String message) {
        super(message);
    }
}