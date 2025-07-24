package com.group3.backend.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.group3.backend.model.Contract;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.model.User;
import com.group3.backend.repository.ContractRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractScheduler {
    private final ContractRepository contractRepository;
    private final ScheduleRepository scheduleRepository;
    private final TreatmentRepository treatmentRepository;
    private final EmailService emailService;

    // Runs every hour
    @Scheduled(cron = "0 0 * * * ?")
    public void checkUnsignedContracts() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Contract> expiredContracts = contractRepository.findByIsSignedAndSignDeadlineLessThan(false, now);

        for (Contract contract : expiredContracts) {
            Treatment treatment = contract.getTreatment();
            User patient = treatment.getPatient();
            
            for (TreatmentPhase phase : treatment.getPhases()) {
                List<Schedule> schedules = scheduleRepository.findByScheduleServicesTreatmentPhaseId(phase.getId());
                for (Schedule schedule : schedules) {
                    schedule.setStatus(Schedule.Status.CANCELLED);
                    scheduleRepository.save(schedule);
                }
            }
            
            // Update treatment status to CANCELLED
            treatment.setStatus(Treatment.Status.CANCELLED);
            treatmentRepository.save(treatment);

            // Send email notification
            String subject = "Hợp đồng điều trị đã hết hạn ký";
            String content = String.format("""
                <p>Chào %s,</p>
                <p>Hợp đồng điều trị của bạn đã hết hạn ký từ <strong>%s</strong>.</p>
                <p>Do đó hợp đồng đã bị hủy. Xin vui lòng liên hệ lại với chúng tôi nếu bạn vẫn muốn điều trị.</p>
                """, patient.getFullName(), contract.getSignDeadline().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

            emailService.sendReminderEmail(patient.getEmail(), subject, content);
        }
    }

    // Nhắc nhở ký hợp đồng sắp đến hạn
    @Scheduled(cron = "0 0 6 * * *") // Chạy mỗi ngày lúc 6 giờ sáng
    public void remindUnsignedContracts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusDays(1).withHour(0).withMinute(0);
        LocalDateTime to = from.plusDays(1);

        List<Contract> upcomingContracts = contractRepository.findByIsSignedFalseAndSignDeadlineBetween(from, to);

        for (Contract contract : upcomingContracts) {
            Treatment treatment = contract.getTreatment();
            User patient = treatment.getPatient();

            String deadline = contract.getSignDeadline().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
            String content = String.format("""
                <p>Chào %s,</p>
                <p>Đây là lời nhắc bạn cần ký hợp đồng điều trị trước <strong>%s</strong>.</p>
                <p>Nếu quá hạn, hợp đồng sẽ bị hủy.</p>
                """, patient.getFullName(), deadline);

            emailService.sendReminderEmail(patient.getEmail(), "Nhắc nhở ký hợp đồng điều trị", content);
        }
    }
}
