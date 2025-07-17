package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.AssignDrug;

@Repository
public interface AssignDrugRepository extends JpaRepository<AssignDrug, UUID> {
    List<AssignDrug> findByIdIn(List<UUID> ids);

    Page<AssignDrug> findByTreatmentPhaseTreatmentPatientIdAndStatusInAndTitleIgnoreCaseContaining(UUID patientId, List<AssignDrug.Status> statuses, String title, Pageable pageable);

    Page<AssignDrug> findByStatusInAndTitleIgnoreCaseContaining(List<AssignDrug.Status> statuses, String title, Pageable pageable);
}
