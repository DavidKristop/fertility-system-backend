package com.group3.backend.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.config.EnvironmentConfig;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.dto.request.Treatment.TreatmentCreateRequest;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseRequest;
import com.group3.backend.dto.request.Treatment.TreatmentScheduleRequest;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Treatment;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.RequestAppointmentService;
import com.group3.backend.service.TreatmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/request-appointments")
@RequiredArgsConstructor
public class RequestAppointmentController {

    private final RequestAppointmentService service;
    private final TreatmentService treatmentService;
    private final EnvironmentConfig environmentConfig;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<RequestAppointment> createRequest(@RequestBody RequestAppointmentRequest request) {
        RequestAppointment result = service.createRequestAppointment(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<RequestAppointment>> getAppointmentsByDoctor(@PathVariable("doctorId") UUID doctorId) {
        // Lấy danh sách các cuộc hẹn theo doctorId
        List<RequestAppointment> appointments = service.getAppointmentsByDoctorId(doctorId);

        // Kiểm tra nếu không có cuộc hẹn nào
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(appointments);
    }

    // API mới để doctor chấp nhận cuộc hẹn
    @PutMapping("/accept/{appointmentId}")
    public ResponseEntity<RequestAppointment> acceptAppointment(@PathVariable UUID appointmentId) {
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
                    .estimatedTime(acceptedAppointment.getAppointmentDatetime())
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
        
        return ResponseEntity.ok(acceptedAppointment);
    }

}
