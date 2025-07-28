package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
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
    private LocalDateTime paymentDate;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<ScheduleService> scheduleServices;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<AssignDrug> assignDrugs;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Refund> refunds;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum PaymentMethod {
        CASH,
        VNPAY
    }
    
    public enum Status {
        PENDING,
        COMPLETED,
        CANCELED
    }
}
