package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    private String password;
}