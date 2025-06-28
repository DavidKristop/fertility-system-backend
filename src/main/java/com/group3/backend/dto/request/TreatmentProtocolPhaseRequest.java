package com.group3.backend.dto.request;

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
import java.util.UUID;

import com.group3.backend.constants.ProtocolAndPhaseConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentProtocolPhaseRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    private String title;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @NotNull(message = "Phase modifier percentage is required")
    @DecimalMin(value = "0.01", message = "Phase modifier percentage must be greater than 0")
    private BigDecimal phaseModifierPercentage;

    @NotNull(message = "Services are required")
    @Size(min = 1, max = ProtocolAndPhaseConstants.MAX_SERVICES_PER_PHASE, message = "Phase must have between 1 and " + ProtocolAndPhaseConstants.MAX_SERVICES_PER_PHASE + " services")
    private List<UUID> serviceIds;

    @NotNull(message = "Drugs are required")
    @Size(min = 1, max = ProtocolAndPhaseConstants.MAX_DRUGS_PER_PHASE, message = "Phase must have between 1 and " + ProtocolAndPhaseConstants.MAX_DRUGS_PER_PHASE + " drugs")
    private List<UUID> drugIds;
}
