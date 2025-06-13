package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/public")
    public ResponseEntity<String> publicAccess() {
        return ResponseEntity.ok("Public content - Accessible by anyone.");
    }

    @GetMapping("/user")
    public ResponseEntity<String> userAccess() {
        return ResponseEntity.ok("User content - Accessible by any authenticated user.");
    }

    @GetMapping("/patient")
    public ResponseEntity<String> patientAccess() {
        return ResponseEntity.ok("Patient content - Accessible only by users with ROLE_PATIENT.");
    }
}