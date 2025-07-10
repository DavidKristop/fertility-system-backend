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

    @Column(name = "is_complete", nullable = false)
    private boolean isComplete;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "phase_modifier_percentage", nullable = false)
    private BigDecimal phaseModifierPercentage;

    @Column(name = "refund_percentage", nullable = false)
    private BigDecimal refundPercentage;

    @OneToOne(mappedBy = "currentPhase")
    private Treatment currentTreatment;

    @ManyToOne
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @OneToMany(mappedBy = "treatmentPhase", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private List<AssignDrug> assignDrugs = new ArrayList<>();

    @OneToMany(mappedBy = "treatmentPhase", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    private List<ScheduleService> scheduleServices = new ArrayList<>();
}
