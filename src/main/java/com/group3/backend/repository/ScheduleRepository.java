package com.group3.backend.repository;

import com.group3.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    @Query("SELECT s FROM Schedule s WHERE s.treatmentPhase.id = :phaseId")
    List<Schedule> findByTreatmentPhaseId(@Param("phaseId") UUID phaseId);
    List<Schedule> findByPatientId(UUID patientId);
    List<Schedule> findByDoctorId(UUID doctorId);

    @Query("FROM Schedule s WHERE s.doctor.id = :doctorId AND s.appointmentDateTime BETWEEN :start AND :end")
    List<Schedule> findByDoctorIdAndAppointmentDateTimeBetween(
        @Param("doctorId") UUID doctorId,
        @Param("start") Timestamp start,
        @Param("end") Timestamp end
    );

    @Query("FROM Schedule s WHERE s.patient.id = :patientId AND s.appointmentDateTime BETWEEN :start AND :end")
    List<Schedule> findByPatientIdAndAppointmentDateTimeBetween(
        @Param("patientId") UUID patientId,
        @Param("start") Timestamp start,
        @Param("end") Timestamp end
    );
}
