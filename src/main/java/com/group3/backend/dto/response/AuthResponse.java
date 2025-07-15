package com.group3.backend.dto.response;


import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String email;
    private String role;
    private String fullName;
    private String address;
    private String phone;
    private LocalDate dateOfBirth;

    private UUID userId;
}