package com.group3.backend.repository;

import com.group3.backend.model.Treatment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<Treatment> findByPatientIdAndStatusIn(
        UUID patientId,
        List<Treatment.Status> status
    );

    Page<Treatment> findByDoctorIdAndStatusInAndPatientEmailIgnoreCaseContaining(
        UUID doctorId,
        List<Treatment.Status> statuses,
        String patientEmail,
        Pageable pageable
    );

    Page<Treatment> findByPatientIdAndStatusInAndDoctorEmailIgnoreCaseContaining(
        UUID patientId,
        List<Treatment.Status> statuses,
        String doctorEmail,
        Pageable pageable
    );

    Page<Treatment> findByPatientEmailContainingIgnoreCaseAndDoctorEmailContainingIgnoreCaseAndStatusIn(
        String patientEmail,
        String doctorEmail,
        List<Treatment.Status> statuses,
        Pageable pageable
    );

    boolean existsByPatientIdAndStatusIn(UUID patientId, List<Treatment.Status> statuses);
}
