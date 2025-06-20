package com.group3.backend.dto.request.Treatment.POST;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import java.util.UUID;

@Data
public class TreatmentCreateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String diagnosis;
    private UUID userId;
    private UUID doctorId;
    private List<TreatmentPhaseRequest> phases;
}
