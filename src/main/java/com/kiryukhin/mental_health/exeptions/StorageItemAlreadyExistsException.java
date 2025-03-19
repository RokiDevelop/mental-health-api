package com.kiryukhin.mental_health.exeptions;

public class StorageItemAlreadyExistsException extends RuntimeException {
    public StorageItemAlreadyExistsException(String message) {
        super(message);
    }
}
