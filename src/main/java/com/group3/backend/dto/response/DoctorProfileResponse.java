package com.group3.backend.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DoctorProfileResponse {
    private String specialty;
    private String degree;
    private BigDecimal yearsOfExperience;
    private String licenseNumber;
}
