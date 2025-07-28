package com.group3.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.CreateManagerRequest;
import com.group3.backend.dto.response.UserRespones;
import com.group3.backend.mapper.UserMapper;
import com.group3.backend.model.User;
import com.group3.backend.service.UserService;
import com.group3.backend.constants.Roles;
import com.group3.backend.service.EmailService;
import com.group3.backend.service.VerifyTokenService;

@RestController
@RequestMapping("/api/manager-management")
public class ManagerManagementController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private VerifyTokenService verifyTokenService;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/new-manager")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response<UserRespones>> createManager(@RequestBody CreateManagerRequest registrationRequest){
        User newUser = userService.registerUser(registrationRequest, Roles.ROLE_MANAGER);
        String token = UUID.randomUUID().toString();
        emailService.sendVerificationEmail(
            newUser.getEmail(), 
            verifyTokenService.createVerifyToken(newUser, token).getToken()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(userMapper.toUserResponse(newUser), "User created successfully"));
    }
}
