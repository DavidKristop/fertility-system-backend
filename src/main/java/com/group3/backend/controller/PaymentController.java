package com.group3.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.config.VNPayConfig;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.PaymentResponse;
import com.group3.backend.mapper.PaymentMapper;
import com.group3.backend.model.Payment;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.VNPayService;
import com.group3.backend.utils.CurrentUserUtils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
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

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private VNPayConfig vnPayConfig;

    @GetMapping("/patient/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<PaymentResponse>> getPaymentByIdAndUserId(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.getPaymentByIdAndUserId(paymentId, currentUserUtils.getCurrentUserId())), "Payment retrieved successfully"));
    }

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<PaymentResponse>>> listPaymentsByUserId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<Payment.Status> status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(paymentService.getPatientPayment(currentUserUtils.getCurrentUserId(), status, pageable).map(paymentMapper::toResponse),
         "Payments retrieved successfully"));
    }


    @GetMapping("/staff")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Response<Page<PaymentResponse>>> listPayments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String email,
        @RequestParam(defaultValue = "PENDING") List<Payment.Status> status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(paymentService.getPaymentByPatientEmail(email, status, pageable).map(paymentMapper::toResponse),
         "Payments retrieved successfully"));
    }

    @GetMapping("/staff/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Response<PaymentResponse>> getPaymentById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.getPaymentById(paymentId)), "Payment retrieved successfully"));
    }

    @PutMapping("/patient/process/vnpay/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<String>> processPayment(@PathVariable UUID paymentId, HttpServletResponse response) throws IOException {
        Payment payment = paymentService.getPaymentByIdAndUserId(paymentId, currentUserUtils.getCurrentUserId());
        
        String redirectUrl = vnPayService.createOrder(payment.getAmount().toBigInteger().intValue(), payment.getDescription(), paymentId);

        return ResponseEntity.ok(new Response<>(redirectUrl,"Redirect url created successfully"));
    }

    @GetMapping("/patient/process/vnpay/return")
    public void processReturnPayment(
        @RequestParam(value = "paymentId") UUID paymentId,
        @RequestParam(value = "vnp_ResponseCode") String vnp_responseCode,
        HttpServletResponse response
    ) throws IOException{
        if(vnp_responseCode.equals("00")){
            paymentService.processPayment(paymentId, Payment.PaymentMethod.VNPAY);
            response.sendRedirect(vnPayConfig.getPayment_success_url()+"?paymentId="+paymentId);
        }
        else{
            response.sendRedirect(vnPayConfig.getPayment_failure_url()+"?paymentId="+paymentId);
        }
    }

    @PutMapping("/staff/process/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Response<PaymentResponse>> processPaymentByStaff(@PathVariable UUID paymentId, @RequestParam Payment.PaymentMethod paymentMethod) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.processPayment(paymentId, paymentMethod)), "Payment processed successfully"));
    }

    @PutMapping("/staff/cancel/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public ResponseEntity<Response<PaymentResponse>> cancelPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(new Response<>(paymentMapper.toResponse(paymentService.cancelPayment(paymentId)), "Payment cancelled successfully"));
    }
}
