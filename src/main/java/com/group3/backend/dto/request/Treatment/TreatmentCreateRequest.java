package com.group3.backend.dto.request.Treatment;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import com.group3.backend.model.Treatment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


@Data
@Builder
public class TreatmentCreateRequest {
    @NotEmpty
    private Treatment.PaymentMode paymentMode;
    @NotEmpty
    private String description;
    @NotNull
    private UUID userId;
    @NotNull
    private UUID protocolId;
}
