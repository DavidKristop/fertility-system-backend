package com.group3.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private UUID userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String specialty;
    private String degree;
    private BigDecimal yearsOfExperience;
    private String licenseNumber;
}