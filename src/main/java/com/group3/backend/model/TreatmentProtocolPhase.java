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

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "refund_condition")
    private String refundCondition;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "position")
    private int position;

    @ManyToOne
    @JoinColumn(name = "treatment_protocol_id", nullable = false)
    private TreatmentProtocol treatmentProtocol;

    @OneToMany(mappedBy = "treatmentProtocolPhase", cascade = CascadeType.ALL)
    private List<TreatmentProtocolService> services;

    @OneToMany(mappedBy = "treatmentProtocolPhase", cascade = CascadeType.ALL)
    private List<TreatmentProtocolDrug> drugs;
}
