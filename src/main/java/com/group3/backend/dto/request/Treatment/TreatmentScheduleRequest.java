package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

import jakarta.validation.constraints.Size;

@Data
@Builder
public class TreatmentScheduleRequest {
    @MinDaysAhead
    @WorkingHours
    private Timestamp appointmentDateTime;
    @MinDaysAhead
    @WorkingHours
    private Timestamp estimatedTime;
    @Size(min = 1, max = 8, message = "Each schedule must have between 1 and 8 services")
    private List<TreatmentServiceRequest> services;
}