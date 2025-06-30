package com.group3.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group3.backend.model.ForgetPasswordToken;
import com.group3.backend.model.User;
import com.group3.backend.repository.ForgetPasswordTokenRepository;
import com.group3.backend.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgetPasswordService {

    private final ForgetPasswordTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public void generateTokenAndSendEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);

        ForgetPasswordToken forgetToken = new ForgetPasswordToken();
        forgetToken.setToken(token);
        forgetToken.setUser(user);
        forgetToken.setExpiration(expiration);

        tokenRepo.save(forgetToken);

        // Gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText("Click here to reset password: http://localhost:5173/reset-password?token=" + token);

        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        ForgetPasswordToken forgetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (forgetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = forgetToken.getUser();
        user.setPasswordHashed(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        tokenRepo.delete(forgetToken); // Xóa token sau khi sử dụng
    }
}

