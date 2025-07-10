package com.group3.backend.dto.request.Treatment;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class TreatmentPhaseSetRequest {
    private UUID phaseId;
    private List<TreatmentScheduleSetRequest> schedules;
    private List<TreatmentAssignDrugSetRequest> assignDrugs;
}
