package com.group3.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.Reminder;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.User;
import com.group3.backend.repository.PatientDrugRepository;
import com.group3.backend.repository.ReminderRepository;
import com.group3.backend.repository.ScheduleRepository;

@Service
public class ReminderService {
    
    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PatientDrugRepository patientDrugRepository;

    @Autowired
    private EmailService emailService;

    public Page<Reminder> getReminderToUser(UUID userId, Pageable pageable) {
        return reminderRepository.findBySendToId(userId, pageable);
    }

    public Reminder updateIsReadReminder(UUID reminderId, UUID userId){
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new IllegalArgumentException("Reminder not found"));
        if(!reminder.getSendTo().getId().equals(userId)){
            throw new UnauthorizedAccessException( "You do not have permission to access this reminder");
        }

        reminder.setRead(true);
        return reminderRepository.save(reminder);
    }

    // Gửi lịch khám ngày mai
    public void sendScheduleReminders() {
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1);

        List<Schedule> schedules = scheduleRepository.findAllByAppointmentDateTimeBetween(startOfTomorrow, endOfTomorrow);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        for (Schedule schedule : schedules) {
            User patient = schedule.getPatient();
            String patientName = patient.getFullName();
            String email = patient.getEmail();
            String formattedTime = schedule.getAppointmentDateTime().format(formatter);

            String html = """
                <p>Chào %s,</p>
                <p>Đây là lời nhắc bạn có một lịch khám vào lúc <strong>%s</strong>.</p>
                <p>Địa điểm: Phòng khám XYZ.</p>
                <p>Vui lòng đến đúng giờ.</p>
                <p>Trân trọng,</p>
                <p>Hệ thống quản lý điều trị hiếm muộn UCARE</p>
            """.formatted(patientName, formattedTime);

            emailService.sendReminderEmail(email, "[Nhắc lịch khám] - Bạn có lịch vào ngày mai", html);
        }
    }

    // Gửi thuốc đang uống
    public void sendMedicationReminders() {
        LocalDate today = LocalDate.now();
        List<PatientDrug> drugs = patientDrugRepository.findByStartDateBeforeAndEndDateAfter(today.plusDays(1), today.minusDays(1));

        for (PatientDrug pd : drugs) {
            User patient = pd.getAssignDrug().getTreatmentPhase().getTreatment().getPatient();
            String patientName = patient.getFullName();
            String email = patient.getEmail();

            String drugName = pd.getDrug().getName();
            String dosage = pd.getDosage();
            String instructions = pd.getUsageInstructions();

            String html = """
                <p>Chào %s,</p>
                <p>Đây là lời nhắc bạn cần uống thuốc: <strong>%s</strong>.</p>
                <p>Liều lượng: <strong>%s</strong></p>
                <p>Hướng dẫn sử dụng: <em>%s</em></p>
                <p>Chúc bạn có nhiều sức khỏe!</p>
                <p>Trân trọng,</p>
                <p>Hệ thống quản lý điều trị hiếm muộn UCARE</p>
            """.formatted(patientName, drugName, dosage, instructions);

            emailService.sendReminderEmail(email, "[Nhắc uống thuốc] - Thuốc cần uống hôm nay", html);
        }
    }

    // Cron mỗi 6h sáng
    @Scheduled(cron = "0 0 6 * * *")
    public void sendDailyReminders() {
        sendScheduleReminders();
        sendMedicationReminders();
    }

}
