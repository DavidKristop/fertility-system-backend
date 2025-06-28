package com.group3.backend.repository;

import com.group3.backend.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {
    List<Treatment> findByPatientId(UUID patientId);
    List<Treatment> findByDoctorId(UUID doctorId);
    Optional<Treatment> findByIdAndPatientId(UUID id, UUID patientId);
    Optional<Treatment> findByIdAndDoctorId(UUID id, UUID doctorId);
}
