package com.group3.backend.dto.request.Treatment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreatmentScheduleSetRequest {
    @NotNull
    private UUID scheduleId;
    @WorkingHours
    @MinDaysAhead
    private LocalDateTime appointmentDateTime;
    @WorkingHours
    @MinDaysAhead
    private LocalDateTime estimatedTime;
    private List<TreatmentScheduleServiceSetRequest> scheduleServices;
}
