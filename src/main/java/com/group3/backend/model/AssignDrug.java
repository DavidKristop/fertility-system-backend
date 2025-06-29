package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "complete_date", nullable = true)
    private LocalDateTime completeDate;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignDrug")
    private List<PatientDrug> patientDrugs;

    public enum Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }

}
