package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import com.group3.backend.model.Treatment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Data
@Builder
public class TreatmentCreateRequest {
    @NotEmpty
    private Treatment.PaymentMode paymentMode;
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;
    @Size(min = 1, max = 500, message = "Medical history must be between 1 and 500 characters")
    private String medicalHistory;

    @Size(min = 1, max=50, message = "Title must be between 1 and 50 characters")
    private String title;
    @NotNull
    private UUID userId;
    @NotNull
    private UUID protocolId;
}
