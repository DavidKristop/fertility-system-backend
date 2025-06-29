package com.group3.backend.dto.response.RequestAppointment;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.model.RequestAppointment;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RequestAppointmentResponse {
    private UUID id;
    private UserDoctorResponse doctor;
    private UserPatientResponse patient;
    private String rejectedReason;
    private LocalDateTime appointmentDatetime;
    private RequestAppointment.Status status;
}
