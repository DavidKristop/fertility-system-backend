package com.group3.backend.dto.response.TreatmentProtocolReponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentProtocolResponse {
    private UUID id;
    private String title;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TreatmentProtocolPhaseResponse> phases;
    private BigDecimal estimatedPrice;
}
