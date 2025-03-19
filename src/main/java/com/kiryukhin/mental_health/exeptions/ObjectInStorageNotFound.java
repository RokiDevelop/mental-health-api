package com.kiryukhin.mental_health.exeptions;

public class ObjectInStorageNotFound extends RuntimeException {
    public ObjectInStorageNotFound(String message) {
        super(message);
    }
}
