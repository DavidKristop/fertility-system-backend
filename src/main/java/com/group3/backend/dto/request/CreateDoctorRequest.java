package com.group3.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import com.group3.backend.constants.Roles;


@Data
@EqualsAndHashCode(callSuper=true)
public class CreateDoctorRequest extends RegistrationRequest {

    // Override role to be DOCTOR
    private Roles role = Roles.ROLE_DOCTOR;

    // DoctorProfile info
    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotNull(message = "Years of experience is required")
    @Digits(integer = 3, fraction = 1, message = "Years of experience must be a valid number with up to 3 digits before the decimal and 1 digits after the decimal")
    private BigDecimal yearsOfExperience;

    @NotBlank(message = "License number is required")
    private String licenseNumber;
}
