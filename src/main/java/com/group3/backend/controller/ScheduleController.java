package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.Treatment.ScheduleResponse;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.model.Schedule;
import com.group3.backend.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleMapper scheduleMapper;

    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Response<List<ScheduleResponse>>> getSchedulesByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorId(doctorId);
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
        
        List<ScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(responses,"Schedules retrieved successfully"));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Response<List<ScheduleResponse>>> getSchedulesByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(patientId);
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
        
        List<ScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(responses,"Schedules retrieved successfully"));
    }

}
