package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.AssignDrug.Status;

@Repository
public interface AssignDrugRepository extends JpaRepository<AssignDrug, UUID> {
    List<AssignDrug> findByIdIn(List<UUID> ids);

    // For patient
    @EntityGraph(attributePaths = {"patientDrugs"})
    Page<AssignDrug> findByTreatmentPhase_Treatment_Patient_IdAndStatus(UUID patientId, Status status, Pageable pageable);
    Page<AssignDrug> findByTreatmentPhase_Treatment_Patient_Id(UUID patientId, Pageable pageable);

    // For manager
    Page<AssignDrug> findByStatusAndTreatmentPhase_Treatment_Patient_FullNameContainingIgnoreCase(Status status, String fullName, Pageable pageable);
    Page<AssignDrug> findByStatus(Status status, Pageable pageable);
    Page<AssignDrug> findByTreatmentPhase_Treatment_Patient_FullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
