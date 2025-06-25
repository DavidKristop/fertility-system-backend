package com.group3.backend.dto.request;


import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class RequestAppointmentRequest {
    @NotEmpty
    private UUID doctorId;
    @NotEmpty
    private UUID patientId;
    private String reason;
    @NotNull(message = "Appointment datetime is required")
    @MinDaysAhead
    @WorkingHours
    private Timestamp appointmentDatetime;
}
