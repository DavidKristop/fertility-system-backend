package com.group3.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.ProfileResponse;
import com.group3.backend.mapper.ProfileMapper;
import com.group3.backend.service.UserService;
import com.group3.backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileMapper profileMapper;
    
    @GetMapping("/me")
    public ResponseEntity<Response<ProfileResponse>> getProfile() {
        return ResponseEntity.ok(new Response<>(profileMapper.toProfileResponse(userService.getUserByEmail(currentUserUtils.getCurrentUser().getEmail())), "Profile retrieved successfully"));
    }
}
