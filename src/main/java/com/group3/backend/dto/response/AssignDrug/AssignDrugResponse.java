package com.group3.backend.dto.response.AssignDrug;

import lombok.Data;
import java.util.List;
import java.util.UUID;

import com.group3.backend.dto.response.ContractPreviewResponse;
import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.dto.response.TreatmentPhasePreviewResponse;
import com.group3.backend.dto.response.TreatmentPreviewResponse;
import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;

@Data
public class AssignDrugResponse {
    private UUID id;
    private String title;
    private String status;
    private UserPatientResponse patient;
    private UserDoctorResponse doctor;
    private PaymentPreviewResponse payment;
    private TreatmentPreviewResponse treatment;
    private TreatmentPhasePreviewResponse treatmentPhase;
    private List<PatientDrugResponse> patientDrugs;
    private ContractPreviewResponse contract;
}
