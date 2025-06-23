package com.group3.backend.service;

import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;
import com.group3.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.exception.ResourceNotFoundException;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TreatmentPhaseRepository treatmentPhaseRepository;
    @Transactional
    public Payment createPayment(PaymentRequest paymentRequest) {
        // Set payment deadline using constant
        Payment payment = Payment.builder()
            .amount(paymentRequest.getAmount())
            .description(paymentRequest.getDescription())
            .paymentDeadline(paymentRequest.getPaymentDeadline())
            .status(Payment.Status.PENDING)
            .user(userRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")))
            .treatmentPhase(treatmentPhaseRepository.findById(paymentRequest.getTreatmentPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment phase not found")))
            .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public void cancelSchedulesForUnpaidPhase(TreatmentPhase phase) {
        // Find all schedules for this phase
        List<Schedule> schedules = scheduleRepository.findByTreatmentPhaseId(phase.getId());
        
        // Cancel all schedules
        schedules.forEach(schedule -> {
            schedule.setStatus(Schedule.Status.CANCELED);
            scheduleRepository.save(schedule);
        });
    }
}
