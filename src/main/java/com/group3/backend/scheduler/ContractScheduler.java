package com.group3.backend.scheduler;

import com.group3.backend.model.Contract;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.repository.ContractRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractScheduler {
    private final ContractRepository contractRepository;
    private final ScheduleRepository scheduleRepository;
    private final TreatmentRepository treatmentRepository;

    // Runs every hour
    @Scheduled(cron = "0 0 * * * ?")
    public void checkUnsignedContracts() {
        // Get current time
        LocalDateTime now = LocalDateTime.now();
        
        // Convert LocalDateTime to Timestamp
        Timestamp deadline = Timestamp.valueOf(now);
        
        // Find all contracts that:
        // 1. Are not signed
        // 2. Have passed their sign deadline
        List<Contract> expiredContracts = contractRepository.findByIsSignedAndSignDeadlineLessThan(false, deadline);

        for (Contract contract : expiredContracts) {
            Treatment treatment = contract.getTreatment();
            
            // Cancel all schedules in all phases of this treatment
            for (TreatmentPhase phase : treatment.getPhases()) {
                List<Schedule> schedules = scheduleRepository.findByTreatmentPhaseId(phase.getId());
                for (Schedule schedule : schedules) {
                    schedule.setStatus(Schedule.Status.CANCELED);
                    scheduleRepository.save(schedule);
                }
            }
            
            // Update treatment status to CANCELLED
            treatment.setStatus(Treatment.Status.CANCELLED);
            treatmentRepository.save(treatment);
        }
    }
}
