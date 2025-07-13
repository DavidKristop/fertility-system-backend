package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.dto.request.Schedule.ScheduleChangeRequest;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.model.Schedule;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.ScheduleService;
import com.group3.backend.utils.CurrentUserUtils;
import com.group3.backend.utils.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @GetMapping("/doctor-schedule/{doctorId}")
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_PATIENT')")
    public ResponseEntity<Response<List<PatientScheduleResponse>>> getSchedulesByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month,
            @RequestParam(required = true) Integer day) {
        try{

            LocalDateTime selectedDate = LocalDateTime.of(year, month, day, 0, 0);
            LocalDateTime startOfDay = selectedDate.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = selectedDate.toLocalDate().atTime(23, 59, 59);
            List<Schedule> schedules = scheduleService.getAppointmentsByDoctorId(doctorId, startOfDay, endOfDay, Schedule.Status.PENDING);
    
            List<PatientScheduleResponse> responses = schedules.stream()
                    .map(scheduleMapper::toPatientScheduleResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new Response<>(
                    responses,
                    "Schedules retrieved successfully"
            ));
        } catch (DateTimeException e) {
            return ResponseEntity.badRequest().body(new Response<>(null, "Invalid date provided"));
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByDoctor(
            @RequestParam(value="from", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate from,
            @RequestParam(value="to", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate to,
            @RequestParam(defaultValue = "PENDING") List<Schedule.Status> status) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorId(currentUserUtils.getCurrentUser().getId(),status,from,to);
        
        List<DoctorScheduleReponse> responses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(
            responses,
            "Schedules retrieved successfully"
        ));
    }

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<List<DoctorScheduleReponse>>> getSchedulesByPatient(
            @RequestParam(value="from", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate from,
            @RequestParam(value="to", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate to,
            @RequestParam(defaultValue = "PENDING") List<Schedule.Status> status) {
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(currentUserUtils.getCurrentUser().getId(),status,from,to);
        
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

    // @PostMapping("/new-schedule")
    // @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    // public ResponseEntity<Response<ScheduleResponse>> addSchedule(@RequestBody AddScheduleToPhaseRequest request) {
    //     Schedule schedule = scheduleService.addScheduleToPhase(request, currentUserUtils.getCurrentUser().getId());
        
    //     BigDecimal totalAmount = schedule.getScheduleServices().stream()
    //             .map(scheduleService -> scheduleService.getService().getPrice())
    //             .reduce(BigDecimal.ZERO, BigDecimal::add);

    //     PaymentRequest paymentRequest = PaymentRequest.builder()
    //         .amount(totalAmount)
    //         .description("Payment for additional schedule")
    //         .paymentDeadline(LocalDateTime.now().plusDays(2))
    //         .userId(currentUserUtils.getCurrentUser().getId())
    //         .scheduleServiceIds(schedule.getScheduleServices().stream()
    //             .map(scheduleService -> scheduleService.getId())
    //             .collect(Collectors.toList()))
    //         .build();

    //     paymentService.createPayment(paymentRequest);

    //     return ResponseEntity.ok(new Response<>(
    //         scheduleMapper.toDoctorScheduleRespone(schedule),
    //         "Schedule created successfully")
    //     );
    // }

    @GetMapping("/patient/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<DoctorScheduleReponse>> getSpecitficScheduleForPatient(
        @PathVariable UUID scheduleId
    ){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        if(!schedule.getPatient().getId().equals(currentUserUtils.getCurrentUserId())) throw new UnauthorizedAccessException("The patient do not have access to this schedule");

        return ResponseEntity.ok(new Response<>(scheduleMapper.toDoctorScheduleRespone(schedule),"Schedule retreive successfully."));
    }

    @GetMapping("/doctor/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<DoctorScheduleReponse>> getSpecitficScheduleForDoctor(
        @PathVariable UUID scheduleId
    ){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        if(!schedule.getDoctor().getId().equals(currentUserUtils.getCurrentUserId())) throw new UnauthorizedAccessException("The patient do not have access to this schedule");

        return ResponseEntity.ok(new Response<>(scheduleMapper.toDoctorScheduleRespone(schedule),"Schedule retreive successfully."));
    }

    @GetMapping("/manager/{scheduleId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<DoctorScheduleReponse>> getSpecitficScheduleForManager(
        @PathVariable UUID scheduleId
    ){
        Schedule schedule = scheduleService.getScheduleById(scheduleId);

        return ResponseEntity.ok(new Response<>(scheduleMapper.toDoctorScheduleRespone(schedule),"Schedule retreive successfully."));
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