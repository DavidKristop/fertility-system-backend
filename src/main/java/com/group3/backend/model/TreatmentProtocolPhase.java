package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatment_protocol_phase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentProtocolPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "phase_modifier_percentage", nullable = false)
    private BigDecimal phaseModifierPercentage;

    @Column(name = "refund_percentage", nullable = false)
    private BigDecimal refundPercentage;

    @ManyToOne
    @JoinColumn(name = "treatment_protocol_id", nullable = false)
    private TreatmentProtocol treatmentProtocol;

    @OneToMany(mappedBy = "treatmentProtocolPhase", cascade = CascadeType.ALL)
    private List<TreatmentProtocolService> services;

    @OneToMany(mappedBy = "treatmentProtocolPhase", cascade = CascadeType.ALL)
    private List<TreatmentProtocolDrug> drugs;
}
