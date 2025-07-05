package com.group3.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.group3.backend.dto.Response;
import com.group3.backend.model.User;
import com.group3.backend.model.VerifyEmailToken;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.repository.VerifyEmailTokenRepository;
import com.group3.backend.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyEmailService {
    private final VerifyEmailTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public Response<String> sendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isVerify()) {
            return new Response<>(null, "Email is already verified", false);
        }

        // Xoá token cũ nếu có
        tokenRepository.deleteByUser(user);
        tokenRepository.flush();

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        VerifyEmailToken verifyToken = new VerifyEmailToken();
        verifyToken.setToken(token);
        verifyToken.setUser(user);
        verifyToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(verifyToken);

        // Gửi email xác thực
        emailService.sendVerificationEmail(user.getEmail(), token);

        return new Response<>(null, "Verification email sent successfully");
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

