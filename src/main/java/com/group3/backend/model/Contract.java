package com.group3.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_signed")
    private Boolean isSigned;

    @Column(name = "sign_deadline")
    private Timestamp signDeadline;

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    @JsonIgnore
    private Treatment treatment;

    @Column(name = "contract_url")
    private String contractUrl;
}
