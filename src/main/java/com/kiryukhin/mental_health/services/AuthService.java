package com.kiryukhin.mental_health.services;

import com.kiryukhin.mental_health.dtos.LoginRequest;
import com.kiryukhin.mental_health.dtos.RefreshTokenRequest;
import com.kiryukhin.mental_health.security.jwt.TokenDto;
import com.kiryukhin.mental_health.security.jwt.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public TokenDto login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));
            return tokenService.generateTokenPairs(loginRequest.getUsername());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public TokenDto refreshToken(@Valid RefreshTokenRequest refreshTokenRequest) {
        return tokenService.generateTokenPairsViaRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}
