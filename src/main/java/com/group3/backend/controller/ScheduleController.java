package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.model.Schedule;
import com.group3.backend.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorId(doctorId,year,month);
        
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/available-doctors/{doctorId}")
    public ResponseEntity<Response<List<PatientScheduleResponse>>> getAvailableDoctors(
            @PathVariable(required = false) UUID doctorId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = new ArrayList<>();
        if(doctorId != null) schedules = scheduleService.getSchedulesByDoctorId(doctorId,year,month);
        else schedules = scheduleService.getAvailableDoctors(year,month);
        
        
        List<PatientScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toPatientScheduleResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Available doctors retrieved successfully"
        ));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(patientId,year,month);
        
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
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
            scheduleMapper.toDoctorScheduleRespone(schedule),
            "Schedule result retrieved successfully")
        );
            
    }

    @PostMapping("/result")
    public ResponseEntity<Response<DoctorScheduleReponse>> addScheduleResult(@RequestBody ScheduleResultRequest request) {
        Schedule schedule = scheduleService.addScheduleResult(request);
        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toDoctorScheduleRespone(schedule),
            "Schedule result added successfully")
        );
    }
}