package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Data
@Builder
public class TreatmentServiceRequest {
    @NotEmpty
    private UUID id;
    @Min(value = 1, message = "Amount of service must be greater than 0")
    private Integer amount;
    private String notes;
}
