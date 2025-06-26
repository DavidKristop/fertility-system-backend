package com.group3.backend.dto.response.Protocol;

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
public class ProtocolPhaseResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal totalAmount;
    private String refundCondition;
    private BigDecimal refundAmount;
    private List<ProtocolServiceResponse> services;
    private List<ProtocolDrugResponse> drugs;
}
