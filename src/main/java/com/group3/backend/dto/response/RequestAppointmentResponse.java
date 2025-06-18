package com.group3.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class RequestAppointmentResponse {

    private UUID id;
    private UUID doctorId;
    private UUID patientId;
    private UUID scheduleId;
    private String reason;
    private String status;
    private LocalDateTime appointmentDatetime;

    // Constructor
    public RequestAppointmentResponse(UUID id, UUID doctorId, UUID patientId, UUID scheduleId, 
                                      String reason, String status, LocalDateTime appointmentDatetime) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.scheduleId = scheduleId;
        this.reason = reason;
        this.status = status;
        this.appointmentDatetime = appointmentDatetime;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getScheduleId() {
        return scheduleId;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAppointmentDatetime() {
        return appointmentDatetime;
    }
}
