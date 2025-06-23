package com.group3.backend.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.group3.backend.config.EnvironmentConfig;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.dto.request.Treatment.TreatmentCreateRequest;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseRequest;
import com.group3.backend.dto.request.Treatment.TreatmentScheduleRequest;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;
import com.group3.backend.dto.response.RequestAppointment.RequestAppointmentResponse;
import com.group3.backend.mapper.AppointmentRequestMapper;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Treatment;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.RequestAppointmentService;
import com.group3.backend.service.TreatmentService;


@RestController
@RequestMapping("/api/request-appointments")
@Validated
public class RequestAppointmentController {

    @Autowired
    private RequestAppointmentService service;
    
    @Autowired
    private TreatmentService treatmentService;
    
    @Autowired
    private EnvironmentConfig environmentConfig;
    
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AppointmentRequestMapper requestAppointmentMapper;

    @PostMapping
    @Validated
    public ResponseEntity<Response<RequestAppointmentResponse>> createRequest(@Valid @RequestBody RequestAppointmentRequest request) {
        RequestAppointment result = service.createRequestAppointment(request);
        return ResponseEntity.ok(new Response<>(
            requestAppointmentMapper.toResponse(result),
            "Request appointment created successfully"));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Response<List<RequestAppointmentResponse>>> getAppointmentsByDoctor(@PathVariable("doctorId") UUID doctorId) {
        List<RequestAppointment> appointments = service.getAppointmentsByDoctorId(doctorId);

        return ResponseEntity.ok(new Response<>(
        appointments.stream().map(requestAppointmentMapper::toResponse).collect(Collectors.toList()),
         "Appointments retrieved successfully"));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Response<List<RequestAppointmentResponse>>> getAppointmentsByPatient(@PathVariable("patientId") UUID patientId) {
        List<RequestAppointment> appointments = service.getAppointmentsByPatientId(patientId);

        return ResponseEntity.ok(new Response<>(
        appointments.stream().map(requestAppointmentMapper::toResponse).collect(Collectors.toList()),
         "Appointments retrieved successfully"));
    }
    

    // API mới để doctor chấp nhận cuộc hẹn
    @PutMapping("/accept/{appointmentId}")
    public ResponseEntity<Response<RequestAppointmentResponse>> acceptAppointment(@PathVariable UUID appointmentId) {
        RequestAppointment acceptedAppointment = service.acceptAppointment(appointmentId);
        
        // Create treatment based on consultation protocol
        TreatmentCreateRequest treatmentRequest = TreatmentCreateRequest.builder()
            .startDate(LocalDate.now())
            .endDate(acceptedAppointment.getAppointmentDatetime().toLocalDateTime().toLocalDate())
            .diagnosis("Consultation")
            .userId(acceptedAppointment.getPatient().getId())
            .doctorId(acceptedAppointment.getDoctor().getId())
            .protocolId(UUID.fromString(environmentConfig.getConsultationProtocolId()))
            .phases(List.of(TreatmentPhaseRequest.builder()
                .title("Consultation Phase")
                .description("Initial consultation phase")
                .position(1)
                .schedules(List.of(TreatmentScheduleRequest.builder()
                    .appointmentDateTime(acceptedAppointment.getAppointmentDatetime())
                    .estimatedTime(new Timestamp(acceptedAppointment.getAppointmentDatetime().getTime() + 45 * 60 * 1000))
                    .services(List.of(TreatmentServiceRequest.builder()
                        .id(UUID.fromString(environmentConfig.getConsultationServiceId()))
                        .amount(1)
                        .build(),
                        TreatmentServiceRequest.builder()
                        .id(UUID.fromString(environmentConfig.getUltrasoundServiceId()))
                        .amount(1)
                        .build()))
                    .build()))
                .build()))
            .build();

        // Create treatment
        Treatment createdTreatment = treatmentService.createTreatment(treatmentRequest);

        // Create payment request
        PaymentRequest paymentRequest = PaymentRequest.builder()
            .amount(createdTreatment.getPhases().get(0).getTotalAmount())
            .description("Consultation payment")
            .paymentDeadline(Timestamp.from(acceptedAppointment.getAppointmentDatetime().toInstant()))
            .userId(acceptedAppointment.getPatient().getId())
            .treatmentPhaseId(createdTreatment.getPhases().get(0).getId())
            .build();

        paymentService.createPayment(paymentRequest);
        
        return ResponseEntity.ok(new Response<>(requestAppointmentMapper.toResponse(acceptedAppointment),
         "Appointment accepted successfully"));
    }

}
