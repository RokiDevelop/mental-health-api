package com.kiryukhin.mental_health.servicesMapping;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.requests.*;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.mappers.RegistrationMapper;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.servicesLogic.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class AuthMappingServiceImpl implements AuthMappingService {

    private final UserMapper userMapper;
    private final RegistrationMapper registrationMapper;
    private final AuthService authService;

    @Override
    public UserResponseDto signUp(RegistrationRequest registrationRequest) {
        UserCreateDto userData = registrationMapper.toUserCreateDto(registrationRequest);
        User user = authService.signUp(userData);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public boolean resetPasswordRequest(PasswordForgetRequest passwordForgetRequest) {
        return authService.resetPasswordRequest(passwordForgetRequest);
    }

    @Override
    public boolean setNewPassword(SetNewPasswordRequest setNewPasswordRequest) {
        return authService.setNewPassword(setNewPasswordRequest);
    }

    @Override
    public TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}