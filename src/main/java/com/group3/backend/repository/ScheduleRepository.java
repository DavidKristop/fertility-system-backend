package com.group3.backend.repository;

import com.group3.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByPatientId(UUID patientId);
    List<Schedule> findByDoctorId(UUID doctorId);
}
