package com.group3.backend.controller;

import com.group3.backend.dto.response.Treatment.TreatmentResponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.Treatment.TreatmentCreateRequest;
import com.group3.backend.model.Treatment;
import com.group3.backend.service.ContractService;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.TreatmentService;
import com.group3.backend.utils.Constants;
import com.group3.backend.utils.CurrentUserUtils;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    
    @Autowired
    private TreatmentService treatmentService;

    @Autowired
    private TreatmentMapper treatmentMapper;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ContractService contractService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<TreatmentResponse>> createTreatment(@RequestBody TreatmentCreateRequest request) throws IOException, UnirestException {
        Treatment treatment = treatmentService.createTreatment(request, currentUserUtils.getCurrentUserId());
        TreatmentResponse response = treatmentMapper.toResponse(treatment);

        contractService.createContract(treatment);
        return ResponseEntity.ok(new Response<>(response, "Treatment created successfully"));
    }
    
    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<TreatmentResponse>>> getAllTreatmentsByPatientId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "IN_PROGRESS") List<Treatment.Status> status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Treatment> treatments = treatmentService.getPatientTreatment(currentUserUtils.getCurrentUserId(), status, title, pageable);
        Page<TreatmentResponse> response = treatments.map(treatmentMapper::toResponse);
        return ResponseEntity.ok(new Response<>(response, "Treatments retrieved successfully"));
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<TreatmentResponse>>> getAllTreatmentsByDoctorId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "IN_PROGRESS") List<Treatment.Status> status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Treatment> treatments = treatmentService.getDoctorTreatment(currentUserUtils.getCurrentUserId(), status, title, pageable);
        Page<TreatmentResponse> response = treatments.map(treatmentMapper::toResponse);
        return ResponseEntity.ok(new Response<>(response, "Treatments retrieved successfully"));
    }

    @GetMapping("/doctor/exist/{patientId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<Boolean>> checkTreatmentExistByDoctorId(
        @PathVariable UUID patientId,
        @RequestParam(defaultValue = "IN_PROGRESS") List<Treatment.Status> status){
        return ResponseEntity.ok(new Response<>(treatmentService.existsByPatientIdAndStatusIn(patientId, status), "Treatment exist"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<TreatmentResponse>>> getAllTreatmentsByManagerId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String title,
        @RequestParam(defaultValue = "IN_PROGRESS") List<Treatment.Status> status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Treatment> treatments = treatmentService.getManagerTreatment(title, status, pageable);
        Page<TreatmentResponse> response = treatments.map(treatmentMapper::toResponse);
        return ResponseEntity.ok(new Response<>(response, "Treatments retrieved successfully"));
    }

    @GetMapping("/patient/{treatmentId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<TreatmentResponse>> getTreatmentById(@PathVariable UUID treatmentId) {
        Treatment treatment = treatmentService.getTreatmentByIdAndPatientId(treatmentId,currentUserUtils.getCurrentUserId());
        TreatmentResponse response = treatmentMapper.toResponse(treatment);
        return ResponseEntity.ok(new Response<>(response, "Treatment retrieved successfully"));
    }

    @GetMapping("/doctor/{treatmentId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<TreatmentResponse>> getTreatmentByIdAndDoctorId(@PathVariable UUID treatmentId) {
        Treatment treatment = treatmentService.getTreatmentByIdAndDoctorId(treatmentId,currentUserUtils.getCurrentUserId());
        TreatmentResponse response = treatmentMapper.toResponse(treatment);
        return ResponseEntity.ok(new Response<>(response, "Treatment retrieved successfully"));
    }

    @GetMapping("/manager/{treatmentId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<TreatmentResponse>> getTreatmentByIdAndManagerId(@PathVariable UUID treatmentId) {
        Treatment treatment = treatmentService.getTreatmentById(treatmentId);
        TreatmentResponse response = treatmentMapper.toResponse(treatment);
        return ResponseEntity.ok(new Response<>(response, "Treatment retrieved successfully"));
    }

    @PutMapping("/next-phase/{treatmentId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<TreatmentResponse>> moveToNextPhase(@PathVariable UUID treatmentId) {
        Treatment treatment = treatmentService.moveToNextPhase(treatmentId);
        
        //Create payment request if payment mode is BY_PHASE
        if(treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)){
            PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(TreatmentService.calculatePhaseEstimatePrice(treatment.getCurrentPhase(), treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)))
                .description("Payment for phase: " + treatment.getCurrentPhase().getTitle())
                .paymentDeadline(LocalDateTime.now().plusHours(Constants.DEADLINE_PAYMENT_DEADLINE_IN_HOURS))
                .userId(currentUserUtils.getCurrentUserId())
                .build();
            paymentService.createPayment(paymentRequest);
        }
        
        return ResponseEntity.ok(new Response<>(
            treatmentMapper.toResponse(treatment),
            "Successfully moved to next phase"));
    }
}
