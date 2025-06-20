package com.group3.backend.dto.response.Treatment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class TreatmentPhaseResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal totalAmount;
    private String refundCondition;
    private BigDecimal refundAmount;
    private List<TreatmentScheduleResponse> schedules;
    private List<TreatmentPatientDrugResponse> patientDrugs;
}
