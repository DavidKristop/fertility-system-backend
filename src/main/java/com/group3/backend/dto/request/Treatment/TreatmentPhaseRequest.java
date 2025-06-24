package com.group3.backend.dto.request.Treatment;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPhaseRequest {
    private String title;
    private String description;
    private List<TreatmentScheduleRequest> schedules;
    private List<TreatmentDrugRequest> drugs;
}
