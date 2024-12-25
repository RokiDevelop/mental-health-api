package com.kiryukhin.mental_health.controllers;

import com.kiryukhin.mental_health.dtos.requests.ActivateAccountRequest;
import com.kiryukhin.mental_health.dtos.requests.ConfirmActionRequest;
import com.kiryukhin.mental_health.dtos.requests.NewPasswordRequest;
import com.kiryukhin.mental_health.dtos.requests.UsernameRequest;
import com.kiryukhin.mental_health.servicesLogic.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
@Validated
public class VerificationController {

    private final AuthService authService;

    @PostMapping("/preliminary-reset-password")
    public ResponseEntity<?> preliminaryResetPassword(@RequestBody @Valid UsernameRequest request) {
        authService.preliminaryResetPassword(request.getUsernameOrEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/preliminary-confirm-action")
    public ResponseEntity<?> preliminaryConfirmAction(Authentication authentication) {
        String username = authentication.getName();

        authService.preliminaryConfirmAction(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/preliminary-activate-account")
    public ResponseEntity<?> preliminaryActivateAccount(@RequestBody @Valid UsernameRequest request) {
        authService.preliminaryActivateAccount(request.getUsernameOrEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestBody @Valid ActivateAccountRequest request) {
        authService.activateAccount(request.getToken());
        return ResponseEntity.ok("Account activated successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Validated NewPasswordRequest newPasswordRequest) {
        authService.resetPassword(newPasswordRequest);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/confirm-action")
    public ResponseEntity<String> confirmAction(@RequestBody @Valid ConfirmActionRequest request) {
        authService.confirmAction(request.getToken());
        return ResponseEntity.ok("Action confirmation successfully");
    }
}
