package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class TreatmentScheduleRequest {
    private Timestamp appointmentDateTime;
    private Timestamp estimatedTime;
    private List<TreatmentServiceRequest> services;
}