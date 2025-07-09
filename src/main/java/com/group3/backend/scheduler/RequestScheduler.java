package com.group3.backend.scheduler;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.repository.RequestAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestScheduler {

    private final RequestAppointmentRepository requestAppointmentRepository;

    @Scheduled(cron = "0 */1 * * * ?") // Runs every minutes
    public void cancelOldAppointments() {
        // Calculate the cutoff time (24 hours ago)
        LocalDateTime cutoffTime = LocalDateTime.now().plusHours(24);
        
        // Find all pending appointments older than 24 hours
        List<RequestAppointment> oldAppointments = requestAppointmentRepository.findByStatusAndAppointmentDatetimeBefore(
            RequestAppointment.Status.PENDING,
            cutoffTime
        );

        // Cancel each old appointment
        for (RequestAppointment appointment : oldAppointments) {
            appointment.setStatus(RequestAppointment.Status.DENIED);
            appointment.setRejectedReason("Appointment request expired. Must be accepted 24h before the appointment.");
            requestAppointmentRepository.save(appointment);
        }

        if (!oldAppointments.isEmpty()) {
            System.out.println("Cancelled " + oldAppointments.size() + " old appointment requests");
        }
    }
}


