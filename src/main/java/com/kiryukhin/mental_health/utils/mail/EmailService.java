package com.kiryukhin.mental_health.utils.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface EmailService {

    void sendRegistrationVerifier(@NotBlank @Email String to, @NotBlank String token);

    void sendUpdatedPassword(@NotBlank @Email String to, @NotBlank @Email String email, @NotBlank String login, @NotBlank String password);

    void sendResetPassword(@NotBlank @Email String to, @NotBlank String token);

    void sendConfirmAction(@NotBlank @Email String to, @NotBlank String token);
}
