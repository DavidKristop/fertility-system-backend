package com.group3.backend.dto.request.Treatment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreatmentPhaseSetRequest {
    @NotNull
    private UUID phaseId;
    private List<TreatmentScheduleSetRequest> schedules = new ArrayList<>();
    private List<TreatmentAssignDrugSetRequest> assignDrugs = new ArrayList<>();
}
