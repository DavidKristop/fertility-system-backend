package com.group3.backend.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProfileResponse extends UserRespones {
    private DoctorProfileResponse doctorProfile;
    private PatientProfileResponse patientProfile;
}
