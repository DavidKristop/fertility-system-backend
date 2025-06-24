package com.group3.backend.dto.request.Treatment;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import com.group3.backend.model.Treatment;


@Data
@Builder
public class TreatmentCreateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Treatment.PaymentMode paymentMode;
    private String diagnosis;
    private UUID userId;
    private UUID doctorId;
    private UUID protocolId;
    private List<TreatmentPhaseRequest> phases;
}
