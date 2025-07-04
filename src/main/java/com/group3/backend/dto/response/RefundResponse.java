package com.group3.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class RefundResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime refundDate;
    private String refundMethod;
    private String reason;
    private UUID userId;
    private UUID paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
