package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.request.ForgotPasswordRequest;
import com.group3.backend.dto.request.ResetPasswordRequest;
import com.group3.backend.service.ForgetPasswordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ForgetPasswordController {
    
    private final ForgetPasswordService forgetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgetPasswordService.generateTokenAndSendEmail(request.getEmail());
        return ResponseEntity.ok("Reset link sent to email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        forgetPasswordService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully.");
}
}
