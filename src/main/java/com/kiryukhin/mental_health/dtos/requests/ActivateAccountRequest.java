package com.kiryukhin.mental_health.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivateAccountRequest {
    @NotBlank
    private String token;
}