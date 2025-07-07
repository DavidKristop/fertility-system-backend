package com.group3.backend.dto.response.Treatment;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.group3.backend.model.Schedule;

@Data
public class TreatmentScheduleResponse {
    private UUID id;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime estimatedTime;
    private Schedule.Status status;
    private List<TreatmentServiceResponse> services;
}
