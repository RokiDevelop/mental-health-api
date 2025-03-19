package com.kiryukhin.mental_health.exeptions;

import jakarta.persistence.EntityExistsException;

public class UserAlreadyAssignedException extends EntityExistsException {
    public UserAlreadyAssignedException(String message) {
        super(message);
    }
}
