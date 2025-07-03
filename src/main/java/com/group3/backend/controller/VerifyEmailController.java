package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.service.VerifyEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify-email")
@RequiredArgsConstructor
public class VerifyEmailController {
    private final VerifyEmailService verifyEmailService;

    @PostMapping("/send")
    public Response<String> sendToken(@RequestParam String email) {
        return verifyEmailService.sendVerificationToken(email);
    }

    @PostMapping("/confirm")
    public Response<String> confirmEmail(@RequestParam String token) {
        return verifyEmailService.verifyEmail(token);
    }
}