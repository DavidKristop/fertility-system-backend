package com.group3.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.constants.Roles;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.CreateDoctorRequest;
import com.group3.backend.dto.response.DoctorResponse;
import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.mapper.UserMapper;
import com.group3.backend.model.User;
import com.group3.backend.service.UserManagementService;
import com.group3.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor-management")
@RequiredArgsConstructor
public class DoctorManagementController {
    private final UserService userService;
    private final UserManagementService userManagementService;
    private final UserMapper userMapper;

    @PostMapping("/new-doctor")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
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

    @GetMapping("/patient/all-doctors")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_STAFF')")
    public ResponseEntity<Response<Page<UserDoctorResponse>>> getAllDoctors(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> doctors = userManagementService.getUsers(Roles.ROLE_DOCTOR, name, true, pageable);
        return ResponseEntity.ok(new Response<>(doctors.map(userMapper::toUserDoctorResponse), "Doctors retrieved successfully", true));
    }

    @GetMapping("/public/doctors")
    public ResponseEntity<Response<Page<UserDoctorResponse>>> getPublicDoctorList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> doctors = userManagementService.getUsers(Roles.ROLE_DOCTOR, name, true, pageable);
        return ResponseEntity.ok(new Response<>(doctors.map(userMapper::toUserDoctorResponse), "Doctors retrieved successfully", true));
    }
}