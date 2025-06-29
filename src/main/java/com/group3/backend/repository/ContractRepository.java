package com.group3.backend.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Contract;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByTreatmentPatientId(UUID patientId);
    List<Contract> findByIsSignedAndSignDeadlineLessThan(Boolean isSigned, Timestamp deadline);
}
