package com.kiryukhin.mental_health.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordForgetRequest {
    @NotBlank private String email;
}