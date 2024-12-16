package com.kiryukhin.mental_health.exeptions;

public class TokenFailedException extends RuntimeException {
    public TokenFailedException(String message) {
        super(message);
    }
}