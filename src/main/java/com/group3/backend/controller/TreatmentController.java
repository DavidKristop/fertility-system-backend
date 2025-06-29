package com.group3.backend.controller;

import com.group3.backend.dto.response.Treatment.TreatmentResponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.Treatment.TreatmentCreateRequestWithContract;
import com.group3.backend.model.Treatment;
import com.group3.backend.service.ContractService;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.TreatmentService;
import com.group3.backend.utils.CurrentUserUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Response<TreatmentResponse>> createTreatment(@RequestBody TreatmentCreateRequestWithContract request) {
        Treatment treatment = treatmentService.createTreatment(request.getTreatmentCreateRequest(), request.getContractCreateRequest().isSigned()?Treatment.Status.COMPLETED:Treatment.Status.IN_PROGRESS);
        TreatmentResponse response = treatmentMapper.toResponse(treatment);

        contractService.createContract(request.getContractCreateRequest(), treatment);

        return ResponseEntity.ok(new Response<>(response, "Treatment created successfully"));
    }
    
    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<List<TreatmentResponse>>> getAllTreatmentsByPatientId() {
        List<Treatment> treatments = treatmentService.getAllTreatmentsByPatientId(currentUserUtils.getCurrentUserId());
        List<TreatmentResponse> response = treatments.stream()
            .map(treatmentMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>(response, "Treatments retrieved successfully"));
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<List<TreatmentResponse>>> getAllTreatmentsByDoctorId() {
        List<Treatment> treatments = treatmentService.getAllTreatmentsByDoctorId(currentUserUtils.getCurrentUserId());
        List<TreatmentResponse> response = treatments.stream()
            .map(treatmentMapper::toResponse)
            .collect(Collectors.toList());
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

    @PostMapping("/{treatmentId}/next-phase")
    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR', 'ROLE_PATIENT')")
    public ResponseEntity<Response<TreatmentResponse>> moveToNextPhase(@PathVariable UUID treatmentId) {
        Treatment treatment = treatmentService.moveToNextPhase(treatmentId);
        
        //Create payment request if payment mode is BY_PHASE
        if(treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)){
            PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(TreatmentService.calculatePhaseEstimatePrice(treatment.getCurrentPhase()))
                .description("Payment for phase: " + treatment.getCurrentPhase().getTitle())
                .paymentDeadline(LocalDateTime.now().plusDays(2))
                .userId(currentUserUtils.getCurrentUserId())
                .build();
            paymentService.createPayment(paymentRequest);
        }
        
        return ResponseEntity.ok(new Response<>(
            treatmentMapper.toResponse(treatment),
            "Successfully moved to next phase"));
    }
}
