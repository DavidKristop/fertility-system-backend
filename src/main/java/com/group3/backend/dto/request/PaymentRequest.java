package com.group3.backend.dto.request;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class PaymentRequest {
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    private BigDecimal amount;
    private String description;
    @NotNull
    private Timestamp paymentDeadline;
    @NotNull
    private UUID userId;
    @Builder.Default
    private List<UUID> scheduleIds = new ArrayList<>();
    @Builder.Default
    private List<UUID> assignDrugIds = new ArrayList<>();
}
