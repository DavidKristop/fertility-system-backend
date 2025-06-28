package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.request.ForgotPasswordRequest;
import com.group3.backend.dto.request.ResetPasswordRequest;
import com.group3.backend.dto.Response;
import com.group3.backend.service.ForgetPasswordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgetPasswordController {
    
    private final ForgetPasswordService forgetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgetPasswordService.generateTokenAndSendEmail(request.getEmail());
        return ResponseEntity.ok(new Response<>(null,"Reset link sent to email.",true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        forgetPasswordService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new Response<>(null,"Password reset successfully.",true));
    }
}

