package com.kiryukhin.mental_health.security.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank private String refreshToken;
}