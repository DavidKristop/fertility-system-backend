package com.group3.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.Response;
import com.group3.backend.model.User;
import com.group3.backend.model.VerifyEmailToken;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.repository.VerifyEmailTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyEmailService {
    private final VerifyEmailTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public Response<String> sendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerify()) {
            return new Response<>(null, "Email is already verified", false);
        }

        // Xoá token cũ nếu có
        tokenRepository.deleteByUser(user);

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        VerifyEmailToken verifyToken = new VerifyEmailToken();
        verifyToken.setToken(token);
        verifyToken.setUser(user);
        verifyToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(verifyToken);

        // Gửi email xác thực
        sendEmail(user.getEmail(), token);

        return new Response<>(null, "Verification email sent successfully");
    }

    private void sendEmail(String toEmail, String token) {
        String link = "http://localhost:5173/verify-email?token=" + token;
        String subject = "Email Verification";
        String text = "Please verify your email by clicking the link below:\n" + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    @Transactional
    public Response<String> verifyEmail(String token) {
        VerifyEmailToken verifyToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verifyToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new Response<>(null, "Token has expired", false);
        }

        User user = verifyToken.getUser();

        if (user.isVerify()) {
            return new Response<>(null, "Email is already verified", false);
        }

        user.setVerify(true);
        userRepository.save(user);
        userRepository.flush();
        tokenRepository.deleteByUser(user); // Clean up

        return new Response<>(null, "Email verified successfully");
    }
}

