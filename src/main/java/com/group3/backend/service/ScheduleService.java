package com.group3.backend.service;

import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleResult;
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

    public Schedule getScheduleById(UUID id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
    }

    public Schedule addScheduleResult(ScheduleResultRequest scheduleResultRequest) {
        Schedule schedule = scheduleRepository.findById(scheduleResultRequest.getScheduleId())
            .orElseThrow(() -> new RuntimeException("Schedule not found"));

        ScheduleResult scheduleResult = ScheduleResult.builder()
            .doctorsNote(scheduleResultRequest.getDoctorsNote())
            .schedule(schedule)
            .build();
        schedule.setScheduleResult(scheduleResult);
        return scheduleRepository.save(schedule);
    }
}
