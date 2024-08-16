package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.LoginRequest;
import com.kiryukhin.mental_health.dtos.RefreshTokenRequest;
import com.kiryukhin.mental_health.security.jwt.TokenDto;
import com.kiryukhin.mental_health.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok().body(authService.refreshToken(refreshTokenRequest));
    }
}
