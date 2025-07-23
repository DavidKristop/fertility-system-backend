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
    @Scheduled(cron = "0 */1 * * * ?")
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
            String subject = "Hợp đồng điều trị đã hết hạn";
            String content = String.format("""
                <p>Chào %s,</p>
                <p>Hợp đồng điều trị của bạn đã hết hạn ký từ <strong>%s</strong>.</p>
                <p>Toàn bộ lịch khám đã bị hủy. Vui lòng liên hệ lại nếu bạn vẫn muốn điều trị.</p>
                """, patient.getFullName(), contract.getSignDeadline().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

            emailService.sendReminderEmail(patient.getEmail(), subject, content);
        }
    }
}
