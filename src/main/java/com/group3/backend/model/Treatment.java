package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Date;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "treatment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "payment_mode", length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;



    @ManyToOne
    @JoinColumn(name = "current_phase_id")
    private TreatmentPhase currentPhase;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "treatment_protocol_id")
    private TreatmentProtocol treatmentProtocol;

    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL)
    private List<TreatmentPhase> phases;


    @OneToOne(mappedBy = "treatment")
    private Contract contract;

    @OneToMany(mappedBy = "treatment")
    private List<Feedback> feedbacks;

    public enum PaymentMode {
        FULL,
        BY_PHASE
    }

    public enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        AWAITING_CONTRACT_SIGNED
    }
}
