package com.kiryukhin.mental_health.exeptions;

import jakarta.persistence.EntityNotFoundException;


public class EntityNotFoundExceptionCustom extends EntityNotFoundException {
    public EntityNotFoundExceptionCustom() {
        super("Entity not found");
    }

    public EntityNotFoundExceptionCustom(Class c) {
        super(c.getSimpleName() + " not found");
    }
}