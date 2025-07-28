package com.group3.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.CreateStaffRequest;
import com.group3.backend.dto.response.UserRespones;
import com.group3.backend.mapper.UserMapper;
import com.group3.backend.model.User;
import com.group3.backend.service.UserService;
import com.group3.backend.constants.Roles;
import com.group3.backend.service.EmailService;
import com.group3.backend.service.VerifyTokenService;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff-management")
public class StaffManagementController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private VerifyTokenService verifyTokenService;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/new-staff")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Response<UserRespones>> createStaff(@RequestBody CreateStaffRequest registrationRequest){
        User newUser = userService.registerUser(registrationRequest, Roles.ROLE_STAFF);
        String token = UUID.randomUUID().toString();
        emailService.sendVerificationEmail(
            newUser.getEmail(), 
            verifyTokenService.createVerifyToken(newUser, token).getToken()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(userMapper.toUserResponse(newUser), "User created successfully"));
    }
    
}
