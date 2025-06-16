package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @Column(name = "refund_condition")
    private String refundCondition;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @OneToMany(mappedBy = "treatmentPhase")
    private List<PatientDrug> patientDrugs;

    @OneToMany(mappedBy = "treatmentPhase")
    private List<ScheduleService> scheduleServices;

    @OneToMany(mappedBy = "treatmentPhase")
    private List<Payment> payments;

    @OneToMany(mappedBy = "treatmentPhase")
    private List<Refund> refunds;
}
