package com.group3.backend.dto.request.Schedule;

import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScheduleCreateRequest {
    @NotEmpty
    private UUID phaseId;
    @MinDaysAhead
    @WorkingHours
    private LocalDateTime appointmentDateTime;
    @MinDaysAhead
    @WorkingHours
    private LocalDateTime estimatedTime;
    @Size(min = 1, max = 8, message = "Each schedule must have between 1 and 8 services")
    private List<TreatmentServiceRequest> services;
}
