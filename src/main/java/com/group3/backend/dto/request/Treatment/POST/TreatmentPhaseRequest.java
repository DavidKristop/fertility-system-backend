package com.group3.backend.dto.request.Treatment.POST;

import java.util.List;

import lombok.Data;

@Data
public class TreatmentPhaseRequest {
    private String title;
    private String description;
    private List<ScheduleRequest> schedules;
    private List<DrugRequest> drugs;
}
