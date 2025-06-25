package com.group3.backend.dto.request.Treatment;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPhaseRequest {
    @NotEmpty
    private String title;
    private String description;
    @NotNull
    private List<TreatmentScheduleRequest> schedules;
    @NotNull
    private List<TreatmentDrugRequest> drugs;
}
