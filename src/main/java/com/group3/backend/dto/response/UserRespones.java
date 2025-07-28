package com.group3.backend.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.group3.backend.constants.Roles;

import lombok.Data;

@Data
public class UserRespones {
    private UUID id;
    private String email;
    private String phone;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String avatarUrl;
    private Roles role;
}
