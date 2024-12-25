package com.kiryukhin.mental_health.dtos.requests;

import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}
