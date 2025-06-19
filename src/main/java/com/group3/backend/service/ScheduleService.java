package com.group3.backend.service;

import com.group3.backend.model.Schedule;
import com.group3.backend.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedulesByDoctorId(UUID doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    public List<Schedule> getSchedulesByPatientId(UUID patientId) {
        return scheduleRepository.findByPatientId(patientId);
    }
}
