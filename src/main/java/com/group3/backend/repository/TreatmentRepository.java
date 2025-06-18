package com.group3.backend.repository;

import com.group3.backend.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {
    Treatment findByPatientId(UUID patientId);
    Treatment findByDoctorId(UUID doctorId);
}
