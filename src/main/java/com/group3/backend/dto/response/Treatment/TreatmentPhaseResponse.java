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
    private boolean isComplete;
    private int position;
    private BigDecimal refundPercentage;
    private BigDecimal phaseModifierPercentage;
    private List<TreatmentScheduleResponse> schedules;
    private List<TreatmentAssignDrugReponse> assignDrugs;
    private List<TreatmentServiceResponse> unsetServices;
}
