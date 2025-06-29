package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.Schedule.ScheduleChangeRequest;
import com.group3.backend.dto.request.Schedule.ScheduleCreateRequest;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.ScheduleService;
import com.group3.backend.utils.CurrentUserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private PaymentService paymentService;

    
    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByDoctor(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorId(currentUserUtils.getCurrentUser().getId(),year,month);
        
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/available-doctors/{doctorId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
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

    @GetMapping("/available-doctors")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_MANAGER', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<Response<List<PatientScheduleResponse>>> getAvailableDoctors(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getAllSchedule(year,month);
        
        List<PatientScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toPatientScheduleResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Available doctors retrieved successfully"
        ));
    }

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByPatient(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(currentUserUtils.getCurrentUser().getId(),year,month);
        
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/result/{scheduleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<Response<ScheduleResponse>> getScheduleResultById(@PathVariable UUID scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        if(schedule.getDoctor().getId() != currentUserUtils.getCurrentUser().getId() || schedule.getPatient().getId() != currentUserUtils.getCurrentUser().getId()) {
            throw new UnauthorizedAccessException("You are not authorized to access this schedule");
        }
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

    @PutMapping("/cancel/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<DoctorScheduleReponse>> cancelSchedule(@PathVariable UUID scheduleId) {
        Schedule schedule = scheduleService.cancelSchedule(scheduleId);

        if(schedule.getPayment().getStatus().equals(Payment.Status.PENDING)){
            paymentService.cancelPayment(schedule.getPayment().getId());
        }

        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toDoctorScheduleRespone(schedule),
            "Schedule canceled successfully")
        );
    }

    @PostMapping("/new-schedule")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<ScheduleResponse>> addSchedule(@RequestBody ScheduleCreateRequest request) {
        Schedule schedule = scheduleService.addScheduleToPhase(request, currentUserUtils.getCurrentUser().getId());
        
        BigDecimal totalAmount = schedule.getScheduleServices().stream()
                .map(scheduleService -> scheduleService.getService().getPrice().multiply(new BigDecimal(scheduleService.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentRequest paymentRequest = PaymentRequest.builder()
            .amount(totalAmount)
            .description("Payment for additional schedule")
            .paymentDeadline(new Timestamp(new Timestamp(System.currentTimeMillis()).getTime() + 2 * 24 * 60 * 60 * 1000))
            .userId(currentUserUtils.getCurrentUser().getId())
            .scheduleIds(List.of(schedule.getId()))
            .build();

        paymentService.createPayment(paymentRequest);

        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toDoctorScheduleRespone(schedule),
            "Schedule created successfully")
        );
    }

    @GetMapping("/today-doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getTodayDoctorSchedules() {
        List<Schedule> schedules = scheduleService.getTodayScheduleForDoctor(currentUserUtils.getCurrentUser().getId());
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone) // dùng mapper chuyển sang DTO phù hợp
                .collect(Collectors.toList());

        return ResponseEntity.ok(new Response<>(responses, "Today's doctor schedules retrieved successfully"));
    }

    @GetMapping("/today-patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<List<PatientScheduleResponse>>> getTodayPatientSchedules() {
        List<Schedule> schedules = scheduleService.getTodayScheduleForPatient(currentUserUtils.getCurrentUser().getId());
        List<PatientScheduleResponse> responses = schedules.stream()
                .map(scheduleMapper::toPatientScheduleResponse) // dùng mapper phù hợp
                .collect(Collectors.toList());

        return ResponseEntity.ok(new Response<>(responses, "Today's patient schedules retrieved successfully"));
    }

    @PutMapping("/done/{id}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<DoctorScheduleReponse>> markScheduleAsDone(@PathVariable UUID id) {
        UUID doctorId = currentUserUtils.getCurrentUser().getId();
        Schedule updated = scheduleService.markScheduleAsDone(id, doctorId);
        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toDoctorScheduleRespone(updated),
            "Schedule marked as DONE successfully"
        ));
    }

    @PutMapping("/change/{id}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<DoctorScheduleReponse>> changeSchedule(
            @PathVariable UUID id,
            @RequestBody ScheduleChangeRequest request) {

        UUID doctorId = currentUserUtils.getCurrentUser().getId();
        Schedule updated = scheduleService.changeScheduleTime(id, doctorId, request);
        return ResponseEntity.ok(new Response<>(
            scheduleMapper.toDoctorScheduleRespone(updated),
            "Schedule updated successfully"
        ));
    }

}