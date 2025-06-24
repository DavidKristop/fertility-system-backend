package com.group3.backend.dto.request.Treatment;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPhaseRequest {
    @NotEmpty
    private String title;
    private String description;
    private List<TreatmentScheduleRequest> schedules;
    private List<TreatmentDrugRequest> drugs;
}
