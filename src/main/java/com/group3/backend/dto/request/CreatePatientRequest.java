package com.group3.backend.dto.request;

import com.group3.backend.constants.Roles;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CreatePatientRequest extends RegistrationRequest {
    private final Roles role = Roles.ROLE_PATIENT;

    @Size(max = 200, message = "Medical history must be at most 200 characters")
    private String medicalHistory;
}
