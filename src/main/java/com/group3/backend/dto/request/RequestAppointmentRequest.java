package com.group3.backend.dto.request;


import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RequestAppointmentRequest {
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;
    @NotNull(message = "Appointment datetime is required")
    @MinDaysAhead
    @WorkingHours
    private LocalDateTime appointmentDatetime;
}
