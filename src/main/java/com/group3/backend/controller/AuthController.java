package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.LoginRequest;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.model.User;
import com.group3.backend.service.JwtService;
import com.group3.backend.service.UserDetailsImpl;
import com.group3.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            String result = service.registerUser(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during user registration: " + e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Response<AuthResponse>> signin(@RequestBody LoginRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new Response<>(null, "Invalid email or password.", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(null, "An unexpected error occurred: " + e.getMessage(), false));
        }

        if (authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtService.generateToken(authRequest.getEmail(), 
                UUID.fromString(userDetails.getUser().getId().toString()), 
                userDetails.getUser().getRole().getName().name());

            //  TRẢ VỀ JSON TOKEN + EMAIL
            AuthResponse authResponse = new AuthResponse(token, 
            authRequest.getEmail(), 
            userDetails.getUser().getRole().getName().name(), 
            userDetails.getUser().getId());
            return ResponseEntity.ok(new Response<>(authResponse, "Authentication successful", true));
        } else {
            return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new Response<>(null, "Authentication failed!", false));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<Response<AuthResponse>> authMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        AuthResponse authResponse = new AuthResponse(null,
        user.getEmail(),
        user.getRole().getName().name(),
        user.getId());
        return ResponseEntity.ok(new Response<>(authResponse, "Authentication successful", true));
    }
}