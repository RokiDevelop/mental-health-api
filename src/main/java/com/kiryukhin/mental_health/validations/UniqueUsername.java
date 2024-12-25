package com.kiryukhin.mental_health.validations;

import com.kiryukhin.mental_health.repositories.UserRepository;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "Username is already taken";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !userRepository.existsByUsername(username);
    }
}