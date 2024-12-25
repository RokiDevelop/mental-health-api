package com.kiryukhin.mental_health.utils.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${frontend.base-url}")
    private String FRONTEND_BASE_URL;

    @Value("${frontend.endpoints.confirm-action-endpoint}")
    private String CONFIRM_ACTION_ENDPOINT;

    @Value("${frontend.endpoints.reset-password-endpoint}")
    private String RESET_PASSWORD_ENDPOINT;

    @Value("${frontend.endpoints.activate-account-endpoint}")
    private String ACTIVATE_ACCOUNT_ENDPOINT;

    private final JavaMailSender mailSender;

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@yourdomain.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("noreply@mentalhealth.com", "Mental Health");
            helper.setTo(to);
            helper.setSubject(String.format("< subject>"));
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendRegistrationVerifier(String to, String token) {
        String subject = "Подтверждение регистрации";
        String url = FRONTEND_BASE_URL + ACTIVATE_ACCOUNT_ENDPOINT + "?token=" + token;
        String message = String.format(
                "<h1>Добро пожаловать!</h1>" +
                        "<p>Для подтверждения регистрации перейдите по ссылке: " +
                        "<a href='%s'>Подтвердить регистрацию</a></p>",
                url
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("Email sending success!");
    }

    @Override
    public void sendUpdatedPassword(String to, String email, String login, String password) {
        String subject = "Пароль обновлён";
        String message = String.format(
                "<h1>Пароль успешно обновлён!</h1>" +
                        "<p>Данные вашего аккаунта:</p>" +
                        "<lo>" +
                        "<li> Email: %s<li>" +
                        "<li> Login: %s<li>" +
                        "<li> Password: %s<li>" +
                        "</lo>",
                email, login, password
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("sendUpdatedPassword success!");
    }

    @Override
    public void sendResetPassword(String to, String token) {
        String subject = "Запрос на обновление пароля";
        String url = FRONTEND_BASE_URL + RESET_PASSWORD_ENDPOINT + "?token=" + token;
        String message = String.format(
                "<h1>Запрос на обновление пароля:</h1>" +
                        "<p>Для обновление пароля перейдите по ссылке и укажите новый пароль: " +
                        "<a href='%s'>Сбросить пароль</a></p>",
                url
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("sendResetPassword success!");
    }

    @Override
    public void sendConfirmAction(String to, String token) {
        String subject = "Запрос на подтверждение действия";
        String url = FRONTEND_BASE_URL + CONFIRM_ACTION_ENDPOINT + "?token=" + token;
        String message = String.format(
                "<h1>Запрос на подтверждение действия:</h1>" +
                        "<p>Для подтверждения действия перейдите по ссылке:" +
                        "<a href='%s'>Подтвердить действие</a></p>",
                url
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("sendResetPassword success!");
    }
}
