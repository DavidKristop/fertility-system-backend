package com.group3.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.group3.backend.dto.response.Treatment.TreatmentAssignDrugReponse;
import com.group3.backend.dto.response.Treatment.TreatmentScheduleResponse;
import com.group3.backend.model.Payment;

import lombok.Data;

@Data
public class PaymentResponse {
    private UUID id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime paymentDate;
    private LocalDateTime paymentDeadline;
    private String paymentMethod;
    private Payment.Status status;
    private UUID userId;
    private List<TreatmentScheduleResponse> schedules;
    private List<TreatmentAssignDrugReponse> assignDrugs;
    private List<RefundResponse> refunds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
