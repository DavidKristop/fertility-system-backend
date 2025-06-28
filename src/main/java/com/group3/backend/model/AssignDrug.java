package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "assign_drug")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDrug {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id", nullable = false)
    private TreatmentPhase treatmentPhase;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
