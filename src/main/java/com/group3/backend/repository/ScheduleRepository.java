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

    List<Schedule> findByDoctorIdAndStatus(
        UUID id,
        Schedule.Status status
    );

    List<Schedule> findByDoctorIdAndStatusIn(
        UUID doctorId,
        List<Schedule.Status> status
    );

    List<Schedule> findByDoctorIdAndAppointmentDateTimeBetweenAndStatusIn(
        UUID doctorId,
        LocalDateTime start,
        LocalDateTime end,
        List<Schedule.Status> status
    );

    List<Schedule> findByPatientIdAndAppointmentDateTimeBetweenAndStatusIn(
        UUID patientId,
        LocalDateTime start,
        LocalDateTime end,
        List<Schedule.Status> status
    );

    List<Schedule> findByPatientIdAndStatusIn(
        UUID patientId,
        List<Schedule.Status> status
    );

    List<Schedule> findByDoctorIdAndStatusAndIdNot(
        UUID doctorId,
        Schedule.Status status,
        UUID id
    );

    List<Schedule> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Schedule> findAllByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
