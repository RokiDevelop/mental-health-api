package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.requests.NewPasswordRequest;
import com.kiryukhin.mental_health.dtos.requests.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.requests.RegistrationRequest;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public interface AuthService {
    @Transactional
    UserResponseDto signUp(RegistrationRequest userCreateDto);

    void preliminaryResetPassword(String username);

    void resetPassword(NewPasswordRequest newPasswordRequest);

    TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest);

    void activateAccount(@NotBlank String token);

    void confirmAction(@NotBlank String token);

    void preliminaryConfirmAction(String username);

    void preliminaryActivateAccount(String usernameOrEmail);
}
