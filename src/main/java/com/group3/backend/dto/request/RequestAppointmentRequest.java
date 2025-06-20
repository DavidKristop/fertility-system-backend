package com.group3.backend.dto.request;


import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class RequestAppointmentRequest {
    private UUID doctorId;
    private UUID patientId;
    private String reason;
    private Timestamp appointmentDatetime;
}
