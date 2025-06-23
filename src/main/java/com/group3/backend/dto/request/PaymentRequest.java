package com.group3.backend.dto.request;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class PaymentRequest {
    private BigDecimal amount;
    private String description;
    private Timestamp paymentDeadline;
    private UUID userId;
    private UUID treatmentPhaseId;
}
