package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/create-manager-account")
    public ResponseEntity<?> createManager(@RequestBody RegistrationRequest request) {
        try {
            String result = userService.createManagerAccount(request);
            return ResponseEntity.ok(new Response<String>("Manager account created", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<String>("Error", e.getMessage()));
        }
    }
}