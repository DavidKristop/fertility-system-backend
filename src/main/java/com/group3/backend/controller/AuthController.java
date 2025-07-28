package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.constants.Roles;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.CreatePatientRequest;
import com.group3.backend.dto.request.LoginRequest;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.User;
import com.group3.backend.service.AuthService;
import com.group3.backend.service.UserDetailsImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<AuthResponse>> signup(@Valid @RequestBody CreatePatientRequest registrationRequest, HttpServletResponse response) {
        if(registrationRequest.getRole() != Roles.ROLE_PATIENT){
            throw new UnauthorizedAccessException("Invalid role");
        }
        return service.signup(registrationRequest, response);
    }

    @PostMapping("/signin")
    public ResponseEntity<Response<AuthResponse>> signin(@RequestBody LoginRequest authRequest, HttpServletResponse response) {
        return service.signin(authRequest, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<AuthResponse>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return service.refreshToken(refreshToken);
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
                user.getFullName(),
                user.getAddress(),
                user.getPhone(),
                user.getDateOfBirth(),
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