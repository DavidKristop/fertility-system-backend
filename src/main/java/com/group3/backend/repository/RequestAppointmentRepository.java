package com.group3.backend.repository;

import com.group3.backend.model.RequestAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestAppointmentRepository extends JpaRepository<RequestAppointment, UUID> {
}