package com.group3.backend.dto.response;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDoctorResponse extends UserRespones {
    private String specialty;
    private String degree;
    private BigDecimal yearsOfExperience;
    private String licenseNumber;
}
