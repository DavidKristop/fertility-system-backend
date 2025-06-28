package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.LoginRequest;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.model.User;
import com.group3.backend.service.JwtService;
import com.group3.backend.service.UserDetailsImpl;
import com.group3.backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<Response<AuthResponse>> signup(@Valid @RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
        try {
            User newUser = service.registerUser(registrationRequest);

            String accessToken = jwtService.generateAccessToken(newUser.getEmail(), newUser.getId(), newUser.getRole().getName().name());
            String refreshToken = jwtService.generateRefreshToken(newUser.getEmail());

            // Set refresh token vào cookie
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            
            response.addCookie(cookie);

            AuthResponse authResponse = new AuthResponse(
            accessToken,
            newUser.getEmail(),
            newUser.getRole().getName().name(),
            newUser.getId()
        );

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(authResponse, "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Registration Error: " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, "An unexpected error occurred during user registration: " + e.getMessage(), false));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Response<AuthResponse>> signin(@RequestBody LoginRequest authRequest, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(null, "Invalid email or password.", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(null, "An unexpected error occurred: " + e.getMessage(), false));
        }

        if (authentication.isAuthenticated()) {
            User user = service.getUserByEmail(authRequest.getEmail());
            String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().getName().name());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());

            // Set refresh token as HttpOnly cookie
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            
            response.addCookie(cookie);

            AuthResponse authResponse = new AuthResponse(
                accessToken, 
                user.getEmail(), 
                user.getRole().getName().name(), 
                user.getId()
            );

            return ResponseEntity.ok(new Response<>(authResponse, "Authentication successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(null, "Authentication failed!", false));
        }
    }

        @PostMapping("/refresh")
    public ResponseEntity<Response<AuthResponse>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Refresh token is missing", false));
        }

        try {
            String email = jwtService.extractEmail(refreshToken);
            User user = service.getUserByEmail(email);

            if (!jwtService.isTokenExpired(refreshToken)) {
                String newAccessToken = jwtService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().getName().name());

                // Trả về access token mới, giữ nguyên refresh token
                AuthResponse authResponse = new AuthResponse(
                    newAccessToken, 
                    email, user.getRole().getName().name(), 
                    user.getId()
                );

                return ResponseEntity.ok(new Response<>(authResponse, "Token refreshed successfully", true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(null, "Refresh token expired", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(null, "Invalid refresh token", false));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<Response<AuthResponse>> authMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        AuthResponse authResponse = new AuthResponse(
                null,
                user.getEmail(),
                user.getRole().getName().name(),
                user.getId()
            );
        return ResponseEntity.ok(new Response<>(authResponse, "Authentication successful", true));
    }

        @GetMapping("/validate")
    public ResponseEntity<Response<String>> validateToken() {
        return ResponseEntity.ok(new Response<>("Token is valid", "Token validation successful", true));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // xóa cookie

        response.addCookie(cookie);

        return ResponseEntity.ok(new Response<>("Logged out successfully", "Logout success", true));
    }
}