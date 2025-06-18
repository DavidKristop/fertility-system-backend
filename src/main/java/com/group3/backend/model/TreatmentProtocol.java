package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "treatment_protocol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentProtocol {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "treatmentProtocol", cascade = CascadeType.ALL)
    private List<TreatmentProtocolPhase> phases;

    @OneToMany(mappedBy = "treatmentProtocol")
    private List<Treatment> treatments;
}
