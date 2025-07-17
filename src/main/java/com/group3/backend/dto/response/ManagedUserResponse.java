package com.group3.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagedUserResponse {
    private UUID id;
    private String email;
    private String phone;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private boolean isVerify;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

