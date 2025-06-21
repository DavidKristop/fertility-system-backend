package com.group3.backend.service;

import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleResult;
import com.group3.backend.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedulesByDoctorId(UUID doctorId, Integer year, Integer month) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        schedules = schedules.stream()
                .filter(schedule -> {
                    LocalDate appointmentDate = schedule.getAppointmentDateTime().toLocalDateTime().toLocalDate();
                    return !appointmentDate.isBefore(startOfMonth) && !appointmentDate.isAfter(endOfMonth);
                })
                .collect(Collectors.toList());
        
        return schedules;
    }

    public List<Schedule> getSchedulesByPatientId(UUID patientId, Integer year, Integer month) {
        List<Schedule> schedules = scheduleRepository.findByPatientId(patientId);
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        schedules = schedules.stream()
                .filter(schedule -> {
                    LocalDate appointmentDate = schedule.getAppointmentDateTime().toLocalDateTime().toLocalDate();
                    return !appointmentDate.isBefore(startOfMonth) && !appointmentDate.isAfter(endOfMonth);
                })
                .collect(Collectors.toList());
        return schedules;
    }

    public Schedule getScheduleById(UUID id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
    }

    public Schedule addScheduleResult(ScheduleResultRequest scheduleResultRequest) {
        Schedule schedule = scheduleRepository.findById(scheduleResultRequest.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        ScheduleResult scheduleResult = ScheduleResult.builder()
            .doctorsNote(scheduleResultRequest.getDoctorsNote())
            .schedule(schedule)
            .build();
        schedule.setScheduleResult(scheduleResult);
        return scheduleRepository.save(schedule);
    }
}
