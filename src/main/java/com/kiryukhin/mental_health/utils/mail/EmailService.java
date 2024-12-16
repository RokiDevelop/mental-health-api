package com.kiryukhin.mental_health.utils.mail;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);

    void sendHtmlEmail(String to, String subject, String htmlBody);

    void sendRegistrationVerifier(String to, String confirmationLink);

    void sendUpdatedPassword(String to, String email, String login, String password);

    void sendResetPassword(String to, String confirmationLink);
}
