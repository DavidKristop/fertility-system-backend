package com.group3.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.group3.backend.model.Payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPreviewResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime paymentDeadline;
    private Payment.Status status;
}
