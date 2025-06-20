package com.group3.backend.repository;

import com.group3.backend.model.ScheduleService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleServiceRepository extends JpaRepository<ScheduleService, UUID> {
    List<ScheduleService> findByTreatmentPhaseId(UUID treatmentPhaseId);
    ScheduleService findByServiceId(UUID serviceId);
}
