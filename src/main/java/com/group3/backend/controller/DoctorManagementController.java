package com.group3.backend.controller;

import com.group3.backend.dto.request.CreateDoctorRequest;
import com.group3.backend.dto.response.DoctorResponse;
import com.group3.backend.dto.Response;
import com.group3.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctor-management")
@RequiredArgsConstructor
public class DoctorManagementController {
    private final UserService userService;

    @PostMapping("/new-doctor")
    public ResponseEntity<Response<DoctorResponse>> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        try {
            DoctorResponse createdDoctor = userService.createDoctorAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(createdDoctor, "Doctor created successfully", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(null, e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(null, "Unexpected error: " + e.getMessage(), false));
        }
    }
}