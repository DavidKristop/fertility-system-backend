package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatment_phase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "is_complete", nullable = false)
    private boolean isComplete;

    @Column(name = "position", nullable = false)
    private int position;

    @ManyToOne
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @OneToMany(mappedBy = "treatmentPhase", cascade = CascadeType.ALL)
    private List<PatientDrug> patientDrugs = new ArrayList<>();

    @OneToMany(mappedBy = "treatmentPhase", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @ManyToMany(mappedBy = "treatmentPhase")
    private List<Payment> payments;

    @OneToMany(mappedBy = "treatmentPhase")
    private List<Refund> refunds;
}
