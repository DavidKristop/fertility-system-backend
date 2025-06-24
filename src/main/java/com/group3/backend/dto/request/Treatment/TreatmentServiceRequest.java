package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import jakarta.validation.constraints.Min;

@Data
@Builder
public class TreatmentServiceRequest {
    private UUID id;
    @Min(value = 1, message = "Amount of service must be greater than 0")
    private Integer amount;
    private String notes;
}
