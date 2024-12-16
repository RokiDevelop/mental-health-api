package com.kiryukhin.mental_health.dtos.requests;

import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}
