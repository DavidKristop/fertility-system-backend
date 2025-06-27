package com.group3.backend.service;

import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.repository.PatientDrugRepository;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;
import com.group3.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.exception.ResourceNotFoundException;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PatientDrugRepository patientDrugRepository;
    @Autowired
    private TreatmentPhaseRepository treatmentPhaseRepository;

    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        // Validate payment deadline
        if (paymentRequest.getPaymentDeadline().toLocalDateTime().isBefore(Timestamp.valueOf(java.time.LocalDateTime.now()).toLocalDateTime())) {
            throw new IllegalArgumentException("Payment deadline cannot be in the past");
        }
        // Create payment
        Payment payment = Payment.builder()
            // .amount(paymentRequest.getAmount())
            // .description(paymentRequest.getDescription())
            // .paymentDeadline(paymentRequest.getPaymentDeadline())
            // .status(Payment.Status.PENDING)
            // .user(userRepository.findById(paymentRequest.getUserId())
            //     .orElseThrow(() -> new ResourceNotFoundException("User not found")))
            // .treatmentPhases(treatmentPhaseRepository.findByIdIn(paymentRequest.getTreatmentPhaseIds()))
            // .schedules(scheduleRepository.findByIdIn(paymentRequest.getScheduleIds()))
            // .patientDrugs(patientDrugRepository.findByIdIn(paymentRequest.getPatientDrugIds()))
            .build();

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    @Transactional
    public void processPayment(UUID paymentId, String paymentMethod) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Check if payment is still pending
        if (!payment.getStatus().equals(Payment.Status.PENDING)) {
            throw new IllegalStateException("Payment is already processed");
        }

        // Check if payment deadline has passed
        if (payment.getPaymentDeadline().toLocalDateTime().isBefore(Timestamp.valueOf(java.time.LocalDateTime.now()).toLocalDateTime())) {
            throw new IllegalStateException("Payment deadline has passed");
        }

        // Update payment status and method
        payment.setPaymentDate(Timestamp.valueOf(java.time.LocalDateTime.now()));
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.Status.COMPLETED);

        paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!payment.getStatus().equals(Payment.Status.PENDING)) {
            throw new IllegalStateException("Payment is not pending");
        }

        // Update payment status
        payment.setStatus(Payment.Status.CANCELED);

        return paymentRepository.save(payment);
    }

}
