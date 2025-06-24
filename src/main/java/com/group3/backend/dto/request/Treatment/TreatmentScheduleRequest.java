package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

@Data
@Builder
public class TreatmentScheduleRequest {
    @MinDaysAhead
    @WorkingHours
    private Timestamp appointmentDateTime;
    @MinDaysAhead
    @WorkingHours
    private Timestamp estimatedTime;
    private List<TreatmentServiceRequest> services;
}