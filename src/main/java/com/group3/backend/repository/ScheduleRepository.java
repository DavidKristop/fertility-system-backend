package com.group3.backend.repository;

import com.group3.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    @Query("SELECT s FROM Schedule s WHERE s.treatmentPhase.id = :phaseId")
    List<Schedule> findByTreatmentPhaseId(@Param("phaseId") UUID phaseId);
    List<Schedule> findByPatientId(UUID patientId);
    List<Schedule> findByDoctorId(UUID doctorId);
}
