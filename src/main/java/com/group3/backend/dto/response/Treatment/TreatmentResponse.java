package com.group3.backend.dto.response.Treatment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class TreatmentResponse {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String status;
    private UUID userId;
    private UUID doctorId;
    private UUID protocolId;
    private List<TreatmentPhaseResponse> phases;
}
