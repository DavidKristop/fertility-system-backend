package com.group3.backend.dto.response.Treatment;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
public class TreatmentScheduleResponse {
    private UUID id;
    private Timestamp appointmentDateTime;
    private Timestamp estimatedTime;
    private List<TreatmentServiceResponse> services;
}
