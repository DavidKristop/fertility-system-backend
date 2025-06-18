package com.group3.backend.dto.request.Treatment.POST;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class ScheduleRequest {
    private Timestamp appointmentDateTime;
    private Timestamp estimatedTime;
    private List<ServiceRequest> services;
}
