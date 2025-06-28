package com.group3.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolDrugRequest {
    @NotNull(message = "Drug ID is required")
    private UUID drugId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be positive")
    private int amount;
}
