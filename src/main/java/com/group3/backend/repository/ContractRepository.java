package com.group3.backend.repository;

import com.group3.backend.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
    List<Contract> findByTreatment_Patient_Id(UUID patientId);
}
