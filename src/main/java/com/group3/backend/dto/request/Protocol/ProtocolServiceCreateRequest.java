package com.group3.backend.dto.request.Protocol;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolServiceCreateRequest {
    @Min(value = 1, message = "Amount must be greater than 0")
    private int amount;
    @NotEmpty
    private UUID serviceId;
}
