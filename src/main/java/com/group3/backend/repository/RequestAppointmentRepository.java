package com.group3.backend.repository;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RequestAppointmentRepository extends JpaRepository<RequestAppointment, UUID> {
    List<RequestAppointment> findByDoctorId(UUID doctorId);

    List<RequestAppointment> findByPatientId(UUID patientId);


    List<RequestAppointment> findByStatusAndAppointmentDatetimeAfter(
            RequestAppointment.Status status,
            LocalDateTime deadline);

    List<RequestAppointment> findByStatusAndAppointmentDatetimeBefore(
                RequestAppointment.Status status,
                LocalDateTime deadline);


    boolean existsByDoctorIdAndAppointmentDatetime(UUID doctorId, LocalDateTime appointmentDatetime);

    boolean existsByPatientIdAndStatusIn(UUID patientId, List<RequestAppointment.Status> statuses);

    boolean existsByPatientIdAndScheduleStatusIn(UUID patientId, List<Schedule.Status> statuses);

    Page<RequestAppointment> findByDoctorIdAndPatientEmailContainingIgnoreCaseAndStatusInAndAppointmentDatetimeAfter(
            UUID doctorId,
            String patientEmail,
            List<RequestAppointment.Status> statuses,
            LocalDateTime deadline,
            Pageable pageable);

    Page<RequestAppointment> findByPatientIdAndDoctorEmailContainingIgnoreCaseAndStatusInAndAppointmentDatetimeAfter(
            UUID patientId,
            String doctorEmail,
            List<RequestAppointment.Status> statuses,
            LocalDateTime deadline,
            Pageable pageable);    
}