package com.group3.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.PaymentResponse;
import com.group3.backend.mapper.PaymentMapper;
import com.group3.backend.model.Payment;
import com.group3.backend.service.PaymentService;
import com.group3.backend.utils.CurrentUserUtils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private PaymentMapper paymentMapper;

    @GetMapping("/patient/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<PaymentResponse>> getPaymentByIdAndUserId(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.getPaymentByIdAndUserId(paymentId, currentUserUtils.getCurrentUserId())), "Payment retrieved successfully"));
    }

    @GetMapping("/patient/")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<PaymentResponse>>> listPaymentsByUserId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") Payment.Status status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(paymentService.getPatientPayment(currentUserUtils.getCurrentUserId(), status, pageable).map(paymentMapper::toResponse),
         "Payments retrieved successfully"));
    }


    @GetMapping("/manager/")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<PaymentResponse>>> listPayments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String email,
        @RequestParam(defaultValue = "PENDING") Payment.Status status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(paymentService.getPaymentByPatientEmail(email, status, pageable).map(paymentMapper::toResponse),
         "Payments retrieved successfully"));
    }

    @GetMapping("/manager/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<PaymentResponse>> getPaymentById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.getPaymentByIdAndUserId(paymentId, currentUserUtils.getCurrentUserId())), "Payment retrieved successfully"));
    }

    @PutMapping("/patient/process/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<PaymentResponse>> processPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.processPayment(paymentId, Payment.PaymentMethod.CREDIT_CARD, currentUserUtils.getCurrentUserId())), "Payment processed successfully"));
    }

    @PutMapping("/manager/process/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<PaymentResponse>> processPaymentByManager(@PathVariable UUID paymentId, @RequestParam Payment.PaymentMethod paymentMethod) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.processPayment(paymentId, paymentMethod)), "Payment processed successfully"));
    }

    @PutMapping("/manager/cancel/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<PaymentResponse>> cancelPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.cancelPayment(paymentId)), "Payment canceled successfully"));
    }
}
