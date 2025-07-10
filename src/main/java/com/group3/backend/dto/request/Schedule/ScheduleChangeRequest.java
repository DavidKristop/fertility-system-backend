package com.group3.backend.dto.request.Schedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.group3.backend.constraints.WorkingHours;

@Data
@Builder
public class ScheduleChangeRequest {

    @NotNull(message = "Appointment datetime is required")
    @Future(message = "Appointment datetime must be in the future")
    @WorkingHours(message = "Appointment datetime must be within working hours (8:00 - 17:00)")
    private LocalDateTime appointmentDateTime;

    @NotNull(message = "Estimated time is required")
    @Future(message = "Estimated time must be in the future")
    @WorkingHours(message = "Estimated time must be within working hours (8:00 - 17:00)")
    private LocalDateTime estimatedTime;
}
