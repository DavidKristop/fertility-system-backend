// AuthService.java
package com.group3.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.CreatePatientRequest;
import com.group3.backend.dto.request.LoginRequest;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.model.User;
import com.group3.backend.model.VerifyEmailToken;
import com.group3.backend.repository.VerifyEmailTokenRepository;
import com.group3.backend.utils.Constants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final VerifyTokenService verifyTokenService;
    private final EmailService emailService;

    @Transactional
    public ResponseEntity<Response<AuthResponse>> signin(LoginRequest authRequest, HttpServletResponse response) {
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
            User user = userService.getUserByEmail(authRequest.getEmail());

            if (!user.isVerify()) {

                String token = UUID.randomUUID().toString();
                emailService.sendVerificationEmail(
                    user.getEmail(), 
                    verifyTokenService.createVerifyToken(user, token).getToken()
                );

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new Response<>(null, "Email is not verified. A verification email has been sent.", false));
            }

            String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getId(), user.getRole().getName().name());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(Constants.MAX_REFRESH_TOKEN_COOKIE_AGE);
            response.addCookie(cookie);

            AuthResponse authResponse = new AuthResponse(
                    accessToken,
                    user.getEmail(),
                    user.getRole().getName().name(),
                    user.getFullName(),
                    user.getAddress(),
                    user.getPhone(),
                    user.getDateOfBirth(),
                    user.getId()
            );

            return ResponseEntity.ok(new Response<>(authResponse, "Authentication successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response<>(null, "Authentication failed!", false));
        }
    }

    public ResponseEntity<Response<AuthResponse>> signup(CreatePatientRequest request, HttpServletResponse response) {
        try {
            User user = userService.registerPatientAccount(request);

            String token = UUID.randomUUID().toString();
            emailService.sendVerificationEmail(
                user.getEmail(), 
                verifyTokenService.createVerifyToken(user, token).getToken()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(null, "User registered successfully. Please check your email to verify your account."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Registration Error: " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, "An unexpected error occurred during user registration: " + e.getMessage(), false));
        }
    }

    public ResponseEntity<Response<AuthResponse>> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>(null, "Refresh token is missing", false));
        }

        try {
            // Check expiration BEFORE trying to extract email
            if (jwtService.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response<>(null, "Refresh token expired", false));
            }

            String email = jwtService.extractEmail(refreshToken);
            User user = userService.getUserByEmail(email);

            String newAccessToken = jwtService.generateAccessToken(
                user.getEmail(), user.getId(), user.getRole().getName().name()
            );

            AuthResponse authResponse = new AuthResponse(
                newAccessToken,
                user.getEmail(),
                user.getRole().getName().name(),
                user.getFullName(),
                user.getAddress(),
                user.getPhone(),
                user.getDateOfBirth(),
                user.getId()
            );

            return ResponseEntity.ok(new Response<>(authResponse, "Token refreshed successfully", true));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Response<>(null, "Invalid refresh token", false));
        }
    }
}
