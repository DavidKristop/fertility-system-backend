package com.group3.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentProtocolPhaseResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer position;
    private BigDecimal phaseModifierPercentage;
    private List<ServiceResponse> services;
    private List<DrugResponse> drugs;
}
