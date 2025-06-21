package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.model.Schedule;
import com.group3.backend.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorId(doctorId,year,month);
        
        List<ScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Response<List<ScheduleResponse>>> getSchedulesByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(patientId,year,month);
        
        List<ScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/result/{scheduleId}")
    public ResponseEntity<Response<ScheduleResponse>> getScheduleResultById(@PathVariable UUID scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toResponse(schedule),
            "Schedule result retrieved successfully")
        );
            
    }

    @PostMapping("/result")
    public ResponseEntity<Response<ScheduleResponse>> addScheduleResult(@RequestBody ScheduleResultRequest request) {
        Schedule schedule = scheduleService.addScheduleResult(request);
        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toResponse(schedule),
            "Schedule result added successfully")
        );
    }
}