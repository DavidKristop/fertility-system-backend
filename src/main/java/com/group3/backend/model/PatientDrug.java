package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient_drug")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDrug {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "drug_id")
    private Drug drug;

    @Column(name = "usage_instructions")
    private String usageInstructions;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "assign_drug_id")
    private AssignDrug assignDrug;
}
