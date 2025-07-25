package com.group3.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.service.ReminderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manual-reminders")
@RequiredArgsConstructor
public class ManualReminderController {

    private final ReminderService reminderService;

    @PostMapping("/schedule")
    public ResponseEntity<Response<String>> sendScheduleReminder() {
        reminderService.sendScheduleReminders();
        return ResponseEntity.ok(new Response<>(null,"Đã gửi nhắc lịch khám ngày mai.",true));
    }

    @PostMapping("/medication")
    public ResponseEntity<Response<String>> sendMedicationReminder() {
        reminderService.sendMedicationReminders();
        return ResponseEntity.ok(new Response<>(null,"Đã gửi nhắc uống thuốc hôm nay.",true));
    }
}
