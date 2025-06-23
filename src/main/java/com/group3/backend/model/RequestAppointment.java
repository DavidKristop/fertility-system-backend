package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;
import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

@Entity
@Table(name = "request_appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAppointment {
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
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(name = "reason")
    private String reason;

    @Column(name = "appointment_datetime", nullable = false)
    @MinDaysAhead
    @WorkingHours
    private Timestamp appointmentDatetime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACCEPTED,
        DENIED,
        PENDING
    }
}
