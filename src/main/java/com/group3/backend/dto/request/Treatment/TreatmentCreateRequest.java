package com.group3.backend.dto.request.Treatment;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import com.group3.backend.model.Treatment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Data
@Builder
public class TreatmentCreateRequest {
    @NotEmpty
    private Treatment.PaymentMode paymentMode;
    @NotEmpty
    private String description;
    @NotEmpty
    private UUID userId;
    @NotEmpty
    private UUID doctorId;
    @NotEmpty
    private UUID protocolId;
    @Size(min = 1, max = 8, message = "Treatment must have between 1 and 8 phases")
    private List<TreatmentPhaseRequest> phases;
}
