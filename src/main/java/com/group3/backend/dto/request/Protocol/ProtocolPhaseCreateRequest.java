package com.group3.backend.dto.request.Protocol;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolPhaseCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @DecimalMin(value = "0.00", message = "Total amount must be non-negative")
    private BigDecimal totalAmount;
    private String refundCondition;
    @DecimalMin(value = "0.00", message = "Refund amount must be non-negative")
    @DecimalMax(value = "100.00", message = "Refund amount must be less than or equal to 100")
    private BigDecimal refundAmount;
    @Min(value = 0, message= "Position must be non-negative")
    private int position;
    
    private List<ProtocolServiceCreateRequest> services;
    private List<ProtocolDrugCreateRequest> drugs;
}
