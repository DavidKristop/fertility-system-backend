package com.group3.backend.dto.response.Treatment;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.dto.response.TreatmentProtocolReponse.TreatmentProtocolResponse;

@Data
public class TreatmentResponse {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String status;
    private UserPatientResponse patient;
    private UserDoctorResponse doctor;
    private UUID currentPhaseId;
    private UUID contractId;
    private boolean isSignedContract;
    private TreatmentProtocolResponse protocol;
    private List<TreatmentPhaseResponse> phases;
    private TreatmentPhaseResponse currentPhase;
}
