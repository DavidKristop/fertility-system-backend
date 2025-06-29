package com.group3.backend.scheduler;

import com.group3.backend.model.Payment;
import com.group3.backend.model.Schedule;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentScheduler {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Scheduled(cron = "0 0 * * * ?") // Runs every hour
    public void checkUnpaidPayments() {
        
        // Find all pending payments that have passed their deadline
        List<Payment> unpaidPayments = paymentRepository.findByStatusAndPaymentDeadlineLessThan(
            Payment.Status.PENDING,
            LocalDateTime.now()
        );

        // For each unpaid payment, cancel the schedules
        for (Payment payment : unpaidPayments) {
            paymentService.cancelPayment(payment.getId());
            List<Schedule> pendingSchedules = payment.getSchedules().stream()
                .filter(schedule -> schedule.getStatus().equals(Schedule.Status.PENDING))
                .collect(Collectors.toList());
            // pendingSchedules.addAll(payment.getTreatmentPhases().stream()
            //     .flatMap(treatmentPhase -> scheduleRepository.findByTreatmentPhaseId(treatmentPhase.getId()).stream())
            //     .filter(schedule -> schedule.getStatus().equals(Schedule.Status.PENDING))
            //     .collect(Collectors.toList()));

            // Cancel associated treatment phases and their schedules
            pendingSchedules.forEach(schedule -> {
                schedule.setStatus(Schedule.Status.CANCELLED);
                scheduleRepository.save(schedule);
            });
        }
    }
}
