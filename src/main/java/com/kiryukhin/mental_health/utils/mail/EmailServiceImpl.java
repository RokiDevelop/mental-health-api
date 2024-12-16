package com.kiryukhin.mental_health.utils.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
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

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
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
    public void sendRegistrationVerifier(String to, String confirmationLink) {
        String subject = "Подтверждение регистрации";
        String message = String.format(
                "<h1>Добро пожаловать!</h1>" +
                        "<p>Для подтверждения регистрации перейдите по ссылке: " +
                        "<a href='%s'>Подтвердить регистрацию</a></p>",
                confirmationLink
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
                        "</lo>" +
                        email, login, password
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("sendUpdatedPassword success!");
    }

    @Override
    public void sendResetPassword(String to, String confirmationLink) {
        String subject = "Запрос на обновление пароля";
        String message = String.format(
                "<h1>Запрос на обновление пароля:</h1>" +
                        "<p>Для обновление пароля перейдите перейдите по ссылке и укажите новый пароль: " +
                        "<a href='%s'></a></p>",
                confirmationLink
        );

        sendHtmlEmail(to, subject, message);
        System.out.println("sendResetPassword success!");
    }
}
