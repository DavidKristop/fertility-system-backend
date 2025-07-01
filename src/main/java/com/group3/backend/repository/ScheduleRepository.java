package com.group3.backend.repository;

import com.group3.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByScheduleServicesTreatmentPhaseId(UUID phaseId);
    List<Schedule> findByPatientId(UUID patientId);
    List<Schedule> findByDoctorId(UUID doctorId);

    List<Schedule> findByDoctorIdAndAppointmentDateTimeBetween(
        UUID doctorId,
        LocalDateTime start,
        LocalDateTime end
    );

    List<Schedule> findByPatientIdAndAppointmentDateTimeBetween(
        UUID patientId,
        LocalDateTime start,
        LocalDateTime end
    );

    List<Schedule> findByIdIn(List<UUID> ids);

    List<Schedule> findByDoctorIdAndAppointmentDateTimeBetweenAndStatus(
        UUID doctorId,
        LocalDateTime appointmentDateTimeStart,
        LocalDateTime appointmentDateTimeEnd,
        Schedule.Status status
    );

}
