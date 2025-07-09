package com.group3.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.group3.backend.config.EnvironmentConfig;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.dto.request.ScheduleCreateRequest;
import com.group3.backend.dto.request.ScheduleServiceCreateRequest;
import com.group3.backend.dto.response.RequestAppointment.RequestAppointmentResponse;
import com.group3.backend.mapper.AppointmentRequestMapper;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.User;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.RequestAppointmentService;
import com.group3.backend.service.ScheduleService;
import com.group3.backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/request-appointments")
@Validated
public class RequestAppointmentController {

    @Autowired
    private RequestAppointmentService service;
    
    @Autowired
    private EnvironmentConfig environmentConfig;
    
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    @Qualifier("appointmentRequestMapper")
    private AppointmentRequestMapper requestAppointmentMapper;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @PostMapping
    @Validated
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<RequestAppointmentResponse>> createRequest(@Valid @RequestBody RequestAppointmentRequest request) {
        RequestAppointment result = service.createRequestAppointment(request, currentUserUtils.getCurrentUser().getId());
        return ResponseEntity.ok(new Response<>(
            requestAppointmentMapper.toResponse(result),
            "Request appointment created successfully"));
    }

    @GetMapping("/request-to-me")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<RequestAppointmentResponse>>> getDoctorAppointmentRequest(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String patientEmail,
        @RequestParam(defaultValue = "PENDING") List<RequestAppointment.Status> status
    ) {
        User user = currentUserUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime deadline = LocalDateTime.now();
        Page<RequestAppointment> appointments = service.getDoctorsAppointments(
            user.getId(),
            patientEmail,
            status,
            deadline,
            pageable);

        return ResponseEntity.ok(new Response<>(
        appointments.map(requestAppointmentMapper::toResponse),
         "Appointments retrieved successfully"));
    }

    @GetMapping("/my-request")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<RequestAppointmentResponse>>> getAppointmentsByPatient(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String doctorEmail,
        @RequestParam(defaultValue = "PENDING") List<RequestAppointment.Status> status
    ) {
        User user = currentUserUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime deadline = LocalDateTime.now();
        Page<RequestAppointment> appointments = service.getPatientsAppointments(
            user.getId(),
            doctorEmail,
            status,
            deadline,
            pageable);

        return ResponseEntity.ok(new Response<>(
        appointments.map(requestAppointmentMapper::toResponse),
         "Appointments retrieved successfully"));
    }
    

    // API mới để doctor chấp nhận cuộc hẹn
    @PutMapping("/accept/{appointmentId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<RequestAppointmentResponse>> acceptAppointment(@PathVariable UUID appointmentId) {
        RequestAppointment acceptedAppointment = service.acceptAppointment(appointmentId, currentUserUtils.getCurrentUser().getId());
        
        Schedule schedule = scheduleService.createScheduleBasedOnRequest(acceptedAppointment);

        // Create payment request
        PaymentRequest paymentRequest = PaymentRequest.builder()
            .amount(schedule.getScheduleServices().stream()
                .map(scheduleService -> scheduleService.getService().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .description("Consultation payment")
            .paymentDeadline(LocalDateTime.now().plusDays(2))
            .userId(acceptedAppointment.getPatient().getId())
            .scheduleServiceIds(schedule.getScheduleServices().stream()
                .map(com.group3.backend.model.ScheduleService::getId)
                .collect(Collectors.toList()))
            .build();

        paymentService.createPayment(paymentRequest);
        
        return ResponseEntity.ok(new Response<>(requestAppointmentMapper.toResponse(acceptedAppointment),
         "Appointment accepted successfully"));
    }

    @PutMapping("/cancel/{appointmentId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<RequestAppointmentResponse>> cancelAppointment(@PathVariable UUID appointmentId, @RequestBody String rejectedReason) {
        RequestAppointment cancelledAppointment = service.cancelAppointment(appointmentId, currentUserUtils.getCurrentUser().getId(), rejectedReason);
        return ResponseEntity.ok(new Response<>(requestAppointmentMapper.toResponse(cancelledAppointment),
         "Appointment cancelled successfully"));
    }

}
