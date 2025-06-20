package com.group3.backend.dto.request.Treatment;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class TreatmentScheduleRequest {
    private Timestamp appointmentDateTime;
    private Timestamp estimatedTime;
    private List<TreatmentServiceRequest> services;
}