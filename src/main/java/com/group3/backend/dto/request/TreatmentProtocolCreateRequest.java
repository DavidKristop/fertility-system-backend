package com.group3.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.group3.backend.constants.ProtocolAndPhaseConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentProtocolCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @NotNull(message = "Phases are required")
    @Size(min = 1, max = ProtocolAndPhaseConstants.MAX_PHASES, message = "Protocol must have between 1 and 20 phases")
    private List<TreatmentProtocolPhaseRequest> phases;

    @NotNull(message = "Refund percentage is required")
    @DecimalMin(value = "0.0", message = "Refund percentage must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Refund percentage must be between 0 and 1")
    private BigDecimal refundPercentage;
}
