package com.group3.backend.dto.response.RequestAppointment;

import java.sql.Timestamp;
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
    private String reason;
    private Timestamp appointmentDatetime;
    private RequestAppointment.Status status;
}
