package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.request.LoginRequest;
import com.kiryukhin.mental_health.dtos.request.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.request.RegistrationRequest;
import com.kiryukhin.mental_health.dtos.request.TokenDto;
import com.kiryukhin.mental_health.services.AuthService;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@MultipartConfig
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @ModelAttribute LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<TokenDto> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok().body(authService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<TokenDto> signUp(HttpServletRequest httpRequest,
//                                           @RequestParam(name = "token", required = false) String token,
                                           @ModelAttribute RegistrationRequest registrationRequest) {
        return ResponseEntity.ok().body(authService.signUp(httpRequest, registrationRequest));
    }
}
