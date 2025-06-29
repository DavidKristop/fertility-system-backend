package com.group3.backend.dto.request.Schedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ScheduleChangeRequest {

    @NotNull(message = "Appointment datetime is required")
    @Future(message = "Appointment datetime must be in the future")
    private Timestamp appointmentDateTime;

    @NotNull(message = "Estimated time is required")
    @Future(message = "Estimated time must be in the future")
    private Timestamp estimatedTime;
}
