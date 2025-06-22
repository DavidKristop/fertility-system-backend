package com.group3.backend.controller;

import com.group3.backend.dto.request.RegistrationRequest;
import com.group3.backend.dto.Response;
import com.group3.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor-management")
@RequiredArgsConstructor
public class DoctorManagementController {
    private final UserService userService;

    @PostMapping("/create-doctor-account")
    public ResponseEntity<?> createDoctor(@RequestBody RegistrationRequest request) {
        try {
            String result = userService.createDoctorAccount(request);
            return ResponseEntity.ok(new Response<String>("Doctor account created", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<String>("Error", e.getMessage()));
        }
    }
}