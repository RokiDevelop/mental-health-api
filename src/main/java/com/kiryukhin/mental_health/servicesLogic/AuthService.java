package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.requests.PasswordForgetRequest;
import com.kiryukhin.mental_health.dtos.requests.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.requests.SetNewPasswordRequest;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.models.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

public interface AuthService {
    @Transactional
    User signUp(UserCreateDto userCreateDto);

    boolean resetPasswordRequest(PasswordForgetRequest passwordForgetRequest);

    boolean setNewPassword(SetNewPasswordRequest setNewPasswordRequest);

    TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest);
}
