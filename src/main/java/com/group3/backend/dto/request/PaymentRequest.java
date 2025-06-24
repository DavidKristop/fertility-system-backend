package com.group3.backend.dto.request;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PaymentRequest {
    private BigDecimal amount;
    private String description;
    private Timestamp paymentDeadline;
    private UUID userId;
    @Builder.Default
    private List<UUID> treatmentPhaseIds = new ArrayList<>();
    @Builder.Default
    private List<UUID> scheduleIds = new ArrayList<>();
    @Builder.Default
    private List<UUID> patientDrugIds = new ArrayList<>();
}
