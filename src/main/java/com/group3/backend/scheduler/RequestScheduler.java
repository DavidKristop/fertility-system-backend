package com.group3.backend.scheduler;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestScheduler {

    private final RequestAppointmentRepository requestAppointmentRepository;

    private final EmailService emailService;

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

            String subject = "Yêu cầu đặt lịch đã bị từ chối";
            String content = String.format("""
                <p>Chào %s,</p>
                <p>Yêu cầu đặt lịch khám vào lúc <strong>%s</strong> đã bị từ chối vì không được xác nhận trước 24 giờ.</p>
                <p>Vui lòng gửi yêu cầu lại nếu bạn vẫn muốn khám.</p>
                """,
                appointment.getPatient().getFullName(),
                appointment.getAppointmentDatetime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
            );

            emailService.sendReminderEmail(appointment.getPatient().getEmail(), subject, content);
        }

        if (!oldAppointments.isEmpty()) {
            System.out.println("Cancelled " + oldAppointments.size() + " old appointment requests");
        }
    }
}


