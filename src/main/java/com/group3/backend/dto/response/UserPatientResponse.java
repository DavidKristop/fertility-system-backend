package com.group3.backend.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPatientResponse extends UserRespones {
    private String medicalHistory;   
}
