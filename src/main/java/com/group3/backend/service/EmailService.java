package com.group3.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác minh địa chỉ email";
        String verificationLink = "http://localhost:5173/verify-email?token=" + token;
        String content = """
                <p>Xin chào,</p>
                <p>Vui lòng nhấp vào liên kết bên dưới để xác minh địa chỉ email của bạn:</p>
                <p><a href="%s">Xác minh Email</a></p>
                <p>Liên kết này sẽ hết hạn trong 30 phút.</p>
                """.formatted(verificationLink);

        sendHtmlEmail(to, subject, content);
    }

        private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void sendReminderEmail(String to, String subject, String contentHtml) {
        sendHtmlEmail(to, subject, contentHtml);
    }
}
