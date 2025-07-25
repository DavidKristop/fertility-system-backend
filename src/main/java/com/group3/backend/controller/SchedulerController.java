package com.group3.backend.controller;

import com.group3.backend.scheduler.*;
import com.group3.backend.dto.Response;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manual-scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final PaymentScheduler paymentScheduler;
    private final ContractScheduler contractScheduler;
    private final RequestScheduler requestScheduler;

    @PostMapping("/check-unpaid-payments")
    public ResponseEntity<Response<String>> callCheckUnpaidPayment() {
        paymentScheduler.checkUnpaidPayments();
        return ResponseEntity.ok(new Response<>(null,"Chạy thành công", true));
    }

    @PostMapping("/remind-upcoming-payments")
    public ResponseEntity<Response<String>> callRemindUpcomingPayments() {
        paymentScheduler.remindUpcomingPayments();
        return ResponseEntity.ok(new Response<>(null,"Chạy thành công", true));
    }

    @PostMapping("/cancel-old-appointments")
    public ResponseEntity<Response<String>> callCancelOldAppointments() {
        requestScheduler.cancelOldAppointments();
        return ResponseEntity.ok(new Response<>(null,"Chạy thành công", true));
    }

    @PostMapping("/check-unsigned-contracts")
    public ResponseEntity<Response<String>> callCheckUnsignedContracts() {
        contractScheduler.checkUnsignedContracts();
        return ResponseEntity.ok(new Response<>(null,"Chạy thành công", true));
    }

    @PostMapping("/remind-upcoming-contracts")
    public ResponseEntity<Response<String>> callRemindUpcomingContracts() {
        contractScheduler.remindUnsignedContracts();
        return ResponseEntity.ok(new Response<>(null,"Chạy thành công", true));
    }
}
