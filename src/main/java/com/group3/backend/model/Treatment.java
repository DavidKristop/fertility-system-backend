package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group3.backend.constants.TreatmentStatus;

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

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;


    @Column(name = "status", nullable = false)
    private String status;

    public TreatmentStatus getTreatmentStatus() {
        return TreatmentStatus.fromString(status);
    }

    public void setTreatmentStatus(TreatmentStatus status) {
        this.status = status.getDisplayName();
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "treatment_protocol_id")
    @JsonIgnore
    private TreatmentProtocol treatmentProtocol;

    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TreatmentPhase> phases;

    @OneToOne(mappedBy = "treatment")
    @JsonIgnore
    private Contract contract;

    @OneToMany(mappedBy = "treatment")
    @JsonIgnore
    private List<Feedback> feedbacks;
}
