package com.group3.backend.dto.request.Schedule;

import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;

import lombok.Data;

@Data
public class ScheduleCreateRequest {
    private UUID phaseId;
    @MinDaysAhead
    @WorkingHours
    private Timestamp appointmentDateTime;
    @MinDaysAhead
    @WorkingHours
    private Timestamp estimatedTime;
    private List<TreatmentServiceRequest> services;
}
