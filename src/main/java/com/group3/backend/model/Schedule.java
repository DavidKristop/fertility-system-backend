package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "treatment_phase_id", nullable = false)
    private TreatmentPhase treatmentPhase;

    @Column(name = "appointment_datetime", nullable = false)
    private Timestamp appointmentDateTime;

    @Column(name = "estimated_time")
    private Timestamp estimatedTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL)
    private ScheduleResult scheduleResult;

    @OneToOne(mappedBy = "schedule")
    private RequestAppointment requestAppointment;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleService> scheduleServices = new ArrayList<>();

    public enum Status {
        PENDING,
        CHANGED,
        CANCELLED,
        DONE
    }
}
