package com.group3.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.model.User;
import com.group3.backend.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/new-manager")
    public ResponseEntity<Response<AuthResponse>> createManager(@Valid @RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
        try {
            User newUser = userService.createManagerAccount(registrationRequest);

            AuthResponse authResponse = new AuthResponse(
            null,
            newUser.getEmail(),
            newUser.getRole().getName().name(),
            newUser.getFullName(),
            newUser.getId()
        );

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(authResponse, "Create Manager account successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Create Manager Error: " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, "An unexpected error occurred during user registration: " + e.getMessage(), false));
        }
    }
}