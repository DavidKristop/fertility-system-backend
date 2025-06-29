package com.group3.backend.scheduler;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.repository.RequestAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.sql.Timestamp;

@Component
public class AppointmentScheduler {
    @Autowired
    private RequestAppointmentRepository requestAppointmentRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void checkPendingAppointments() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime deadline = now.plus(1, ChronoUnit.DAYS);

        requestAppointmentRepository.findByStatusAndAppointmentDatetimeBefore(
                RequestAppointment.Status.PENDING,
                LocalDateTime.of(deadline.toLocalDate(), deadline.toLocalTime())
        ).forEach(appointment -> {
            appointment.setStatus(RequestAppointment.Status.DENIED);
            requestAppointmentRepository.save(appointment);
        });
    }
}
