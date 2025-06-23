package com.group3.backend.scheduler;

import com.group3.backend.model.Payment;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class PaymentScheduler {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;

    @Scheduled(cron = "0 0 * * * ?") // Runs every hour
    public void checkUnpaidPayments() {
        // Get current time
        LocalDateTime now = LocalDateTime.now();
        
        // Find all pending payments that have passed their deadline
        List<Payment> unpaidPayments = paymentRepository.findByStatusAndPaymentDeadlineLessThan(
            Payment.Status.PENDING,
            Timestamp.from(now.toInstant(ZoneOffset.UTC))
        );

        // For each unpaid payment, cancel the schedules
        for (Payment payment : unpaidPayments) {
            paymentService.cancelSchedulesForUnpaidPhase(payment.getTreatmentPhase());
            payment.setStatus(Payment.Status.CANCELED);
            paymentRepository.save(payment);
        }
    }
}
