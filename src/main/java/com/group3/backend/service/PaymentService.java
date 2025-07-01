package com.group3.backend.service;

import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.repository.AssignDrugRepository;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private AssignDrugRepository assignDrugRepository;

    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        // Validate payment deadline
        if (paymentRequest.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Payment deadline cannot be in the past");
        }
        // Create payment
        Payment payment = Payment.builder()
            .amount(paymentRequest.getAmount())
            .description(paymentRequest.getDescription())
            .paymentDeadline(paymentRequest.getPaymentDeadline())
            .status(Payment.Status.PENDING)
            .user(userRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")))
            .build();

        // Get schedules and assign drugs
        List<Schedule> schedules = scheduleRepository.findByIdIn(paymentRequest.getScheduleIds());
        List<AssignDrug> assignDrugs = assignDrugRepository.findByIdIn(paymentRequest.getAssignDrugIds());

        // Set payment reference for each schedule and assign drug
        schedules.forEach(schedule -> schedule.setPayment(payment));
        assignDrugs.forEach(assignDrug -> assignDrug.setPayment(payment));

        // Set the relationships in payment
        payment.setSchedules(schedules);
        payment.setAssignDrugs(assignDrugs);

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    public Page<Payment> getPatientPayment(UUID patientId, Payment.Status status, Pageable pageable){
        return paymentRepository.findByUserIdAndStatus(patientId, status, pageable);
    }

    public Page<Payment> getPaymentByPatientEmail(String email, Payment.Status status, Pageable pageable){
        return paymentRepository.findByUserEmailIgnoreCaseContainingAndStatus(email, status, pageable);
    }

    public Payment getPaymentByIdAndUserId(UUID id, UUID userId){
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (!payment.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("You do not own this payment");
        }
        return payment;
    }

    public Payment getPaymentById(UUID id){
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }


    public Payment processPayment(UUID paymentId, Payment.PaymentMethod paymentMethod){
        return processPayment(paymentId, paymentMethod, null);
    }

    @Transactional
    public Payment processPayment(UUID paymentId, Payment.PaymentMethod paymentMethod, UUID userId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Check if payment is still pending
        if (!payment.getStatus().equals(Payment.Status.PENDING)) {
            throw new IllegalStateException("Payment is already processed");
        }

        // Check if payment deadline has passed
        if (payment.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Payment deadline has passed");
        }

        // Check if payment belongs to the user
        if (!payment.getUser().getId().equals(userId)&&userId != null) {
            throw new ResourceNotFoundException("You do not own this payment");
        }

        // Update payment status and method
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.Status.COMPLETED);

        return paymentRepository.save(payment);
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

        // Cancel associated treatment phases and their schedules
        payment.getSchedules().forEach(schedule -> {
            schedule.setStatus(Schedule.Status.CANCELLED);
            scheduleRepository.save(schedule);
        });

        payment.getAssignDrugs().forEach(assignDrug -> {
            assignDrug.setStatus(AssignDrug.Status.CANCELLED);
            assignDrugRepository.save(assignDrug);
        });

        return paymentRepository.save(payment);
    }

}
