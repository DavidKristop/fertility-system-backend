package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

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
}
