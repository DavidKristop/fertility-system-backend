package com.group3.backend.repository;

import com.group3.backend.model.TreatmentPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TreatmentPhaseRepository extends JpaRepository<TreatmentPhase, UUID> {
    List<TreatmentPhase> findByTreatmentId(UUID treatmentId);
    List<TreatmentPhase> findByIdIn(List<UUID> treatmentPhaseIds);
}
