package com.group3.backend.service;

import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleService;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.repository.AssignDrugRepository;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.ScheduleServiceRepository;
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
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignDrugRepository assignDrugRepository;
    @Autowired
    private ScheduleServiceRepository scheduleServiceRepository;

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
        List<ScheduleService> scheduleServices = scheduleServiceRepository.findByIdIn(paymentRequest.getScheduleServiceIds());
        List<AssignDrug> assignDrugs = assignDrugRepository.findByIdIn(paymentRequest.getAssignDrugIds());

        // Set payment reference for each schedule and assign drug
        scheduleServices.forEach(scheduleService -> scheduleService.setPayment(payment));
        assignDrugs.forEach(assignDrug -> assignDrug.setPayment(payment));

        // Set the relationships in payment
        payment.setScheduleServices(scheduleServices);
        payment.setAssignDrugs(assignDrugs);

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    public Page<Payment> getPatientPayment(UUID patientId, List<Payment.Status> statuses, Pageable pageable){
        return paymentRepository.findByUserIdAndStatusIn(patientId, statuses, pageable);
    }

    public Page<Payment> getPaymentByPatientEmail(String email, List<Payment.Status> statuses, Pageable pageable){
        return paymentRepository.findByUserEmailIgnoreCaseContainingAndStatusIn(email, statuses, pageable);
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
            throw new ResourceConflictException("Payment is already processed");
        }

        // Check if payment deadline has passed
        if (payment.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new ResourceConflictException("Payment deadline has passed");
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

        // Cancel associated schedule services
        payment.getScheduleServices().forEach(scheduleService -> {
            if(scheduleService.getSchedule() != null) scheduleService.getSchedule().setStatus(Schedule.Status.CANCELLED);
            scheduleServiceRepository.save(scheduleService);
        });

        payment.getAssignDrugs().forEach(assignDrug -> {
            assignDrug.setStatus(AssignDrug.Status.CANCELLED);
            assignDrugRepository.save(assignDrug);
        });

        return paymentRepository.save(payment);
    }

}
