package com.group3.backend.dto.request.Protocol;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolDrugCreateRequest {
    @Min(value = 1, message = "Amount must be greater than 0")
    private int amount;

    private UUID drugId;
}
