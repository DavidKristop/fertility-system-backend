package com.group3.backend.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.response.AuthResponse;
import com.group3.backend.dto.response.ManagedUserResponse;
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
            newUser.getAddress(),
            newUser.getPhone(),
            newUser.getDateOfBirth(),
            newUser.getId()
        );

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(authResponse, "Create Manager account successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(null, "Create Manager Error: " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(null, "An unexpected error occurred during user registration: " + e.getMessage(), false));
        }
    }

    
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response<Page<ManagedUserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(required = false) String role
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ManagedUserResponse> result = userService.getUsers(role, email, pageable);
        return ResponseEntity.ok(new Response<>(result, "Users fetched successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(new Response<>(null, "User deactivated successfully"));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> reactivateUser(@PathVariable UUID id) {
        userService.reactivateUser(id);
        return ResponseEntity.ok(new Response<>(null, "User reactivated successfully"));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ManagedUserResponse> getUserById(@PathVariable UUID id) {
        ManagedUserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}