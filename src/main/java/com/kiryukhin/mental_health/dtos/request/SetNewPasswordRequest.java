package com.kiryukhin.mental_health.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetNewPasswordRequest {
    @NotBlank private String password;
    @NotBlank private String username;
    @NotBlank private String code;
}