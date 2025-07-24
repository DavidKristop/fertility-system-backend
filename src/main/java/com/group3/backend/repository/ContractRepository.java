package com.group3.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Contract;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByTreatmentPatientId(UUID patientId);
    List<Contract> findByIsSignedAndSignDeadlineLessThan(Boolean isSigned, LocalDateTime deadline);

    Page<Contract> findByIsSignedAndTreatmentPatientId(Boolean isSigned, UUID patientId, Pageable pageable);

    Page<Contract> findByIsSigned(Boolean isSigned, Pageable pageable);

    List<Contract> findByIsSignedFalseAndSignDeadlineBetween(LocalDateTime from, LocalDateTime to);
}
