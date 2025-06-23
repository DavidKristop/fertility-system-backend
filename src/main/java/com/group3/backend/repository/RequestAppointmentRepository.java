package com.group3.backend.repository;

import com.group3.backend.model.RequestAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface RequestAppointmentRepository extends JpaRepository<RequestAppointment, UUID> {
    List<RequestAppointment> findByDoctorId(UUID doctorId);

    @Query("SELECT ra FROM RequestAppointment ra " +
            "WHERE ra.status = :status AND " +
            "ra.appointmentDatetime < :deadline")
    List<RequestAppointment> findByStatusAndAppointmentDatetimeLessThan(
            @Param("status") RequestAppointment.Status status,
            @Param("deadline") Timestamp deadline);

    boolean existsByDoctorIdAndAppointmentDatetime(UUID doctorId, Timestamp appointmentDatetime);
}