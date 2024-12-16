package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationRequest {
    @NotBlank
    @Pattern(regexp = "^[^@]+$", message = "Username must not contain the '@' symbol.")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    private LocalDateTime birthday;
}