package com.group3.backend.dto.request.Treatment;

import java.util.List;


import lombok.Data;

@Data
public class TreatmentPhaseRequest {
    private String title;
    private String description;
    private List<TreatmentScheduleRequest> schedules;
    private List<TreatmentDrugRequest> drugs;
}
