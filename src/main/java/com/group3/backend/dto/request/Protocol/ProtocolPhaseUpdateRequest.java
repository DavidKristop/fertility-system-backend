package com.group3.backend.dto.request.Protocol;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolPhaseUpdateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @DecimalMin(value = "0.00", message = "Total amount must be non-negative")
    private BigDecimal totalAmount;
    private String refundCondition;
    @DecimalMin(value = "0.00", message = "Refund amount must be non-negative")
    private BigDecimal refundAmount;
    private int position;
    
    
}
