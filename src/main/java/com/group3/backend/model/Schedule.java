package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
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
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @Column(name = "appointment_datetime", nullable = false)
    private Timestamp appointmentDateTime;

    @Column(name = "estimated_time")
    private Timestamp estimatedTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "schedule")
    private ScheduleResult scheduleResult;

    @OneToOne(mappedBy = "schedule")
    private RequestAppointment requestAppointment;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleService> scheduleServices;

    public enum Status {
        PENDING,
        CHANGED,
        DONE
    }
}
