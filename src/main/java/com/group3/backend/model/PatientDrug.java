package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Date;
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
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id")
    private TreatmentPhase treatmentPhase;

    @ManyToOne
    @JoinColumn(name = "assign_drug_id")
    private AssignDrug assignDrug;
}
