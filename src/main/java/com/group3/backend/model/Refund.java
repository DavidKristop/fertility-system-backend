package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "refund_date")
    private Timestamp refundDate;

    @Column(name = "refund_method")
    private String refundMethod;

    @ManyToOne
    @JoinColumn(name = "payment_history_id")
    private PaymentHistory paymentHistory;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id")
    private TreatmentPhase treatmentPhase;
}
