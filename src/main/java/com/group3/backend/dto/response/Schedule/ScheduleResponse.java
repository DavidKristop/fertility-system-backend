package com.group3.backend.dto.response.Schedule;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.group3.backend.dto.response.Treatment.TreatmentServiceResponse;
import com.group3.backend.model.Schedule;

import lombok.Data;

@Data
public class ScheduleResponse {
    private UUID id;
    private Timestamp appointmentDateTime;
    private Timestamp estimatedTime;
    private Schedule.Status status;
    private ScheduleResultResponse scheduleResult;
    private List<TreatmentServiceResponse> services;
}
