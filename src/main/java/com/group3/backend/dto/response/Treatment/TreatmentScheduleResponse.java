package com.group3.backend.dto.response.Treatment;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TreatmentScheduleResponse {
    private UUID id;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime estimatedTime;
    private TreatmentServiceResponse service;
}
