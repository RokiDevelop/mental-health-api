package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.requests.RefreshTokenRequest;
import com.kiryukhin.mental_health.dtos.requests.RegistrationRequest;
import com.kiryukhin.mental_health.dtos.requests.TokenDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.servicesMapping.AuthMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthMappingService authMappingService;

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok().body(authMappingService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody RegistrationRequest registrationRequest) {
        UserResponseDto dto = authMappingService.signUp(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
