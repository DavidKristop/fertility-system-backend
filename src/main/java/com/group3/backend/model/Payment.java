package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "payment_date")
    private Timestamp paymentDate;

    @Column(name = "payment_deadline")
    private Timestamp paymentDeadline;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id", nullable = false)
    private TreatmentPhase treatmentPhase;

    public enum Status {
        PENDING,
        COMPLETED,
        CANCELED
    }
}
