package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.RequestAppointment;

public interface RequestAppointmentRepository extends JpaRepository<RequestAppointment, UUID> {
    List<RequestAppointment> findByDoctorId(UUID doctorId);
}
