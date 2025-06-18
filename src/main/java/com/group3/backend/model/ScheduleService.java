package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "schedule_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleService {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private int id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "notes")
    private String notes;

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id")
    private TreatmentPhase treatmentPhase;
}
