package com.group3.backend.controller;

import com.group3.backend.dto.request.LoginRequest;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.service.JwtService;
import com.group3.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

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
    public ResponseEntity<?> signin(@RequestBody LoginRequest authRequest) {
    Authentication authentication;
    try {
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
    } catch (UsernameNotFoundException | BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }

    if (authentication.isAuthenticated()) {
        String token = jwtService.generateToken(authRequest.getEmail());

        //  TRẢ VỀ JSON TOKEN + EMAIL
        AuthResponse authResponse = new AuthResponse(token, authRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed!");
    }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        return ResponseEntity.ok("Token is valid");
    }
}