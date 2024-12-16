package com.kiryukhin.mental_health.servicesMapping;

import com.kiryukhin.mental_health.dtos.requests.*;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

public interface AuthMappingService {
    @Transactional
    UserResponseDto signUp(RegistrationRequest registrationRequest);

    boolean resetPasswordRequest(PasswordForgetRequest passwordForgetRequest);

    boolean setNewPassword(SetNewPasswordRequest setNewPasswordRequest);

    TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest);
}
