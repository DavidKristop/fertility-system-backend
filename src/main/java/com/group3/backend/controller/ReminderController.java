package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.ReminderReponse;
import com.group3.backend.mapper.ReminderMapper;
import com.group3.backend.model.Reminder;
import com.group3.backend.service.ReminderService;
import com.group3.backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/reminder")
public class ReminderController {

    @Autowired
    ReminderService reminderService;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @Autowired
    ReminderMapper reminderMapper;

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_DOCTOR', 'ROLE_MANAGER')")
    public ResponseEntity<Response<Page<ReminderReponse>>> getReminders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = currentUserUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        Page<Reminder> reminders = reminderService.getReminderToUser(userId, pageable);
        Page<ReminderReponse> response = reminders.map(reminderMapper::toResponse);
        return ResponseEntity.ok(new Response<>(response, "Reminders retrieved successfully"));
    }

    @PutMapping("/{reminderId}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_DOCTOR', 'ROLE_MANAGER')")
    public ResponseEntity<Response<ReminderReponse>> updateIsReadReminder(
        @PathVariable UUID reminderId
    ) {
        UUID userId = currentUserUtils.getCurrentUserId();
        Reminder updatedReminder = reminderService.updateIsReadReminder(reminderId, userId);
        ReminderReponse response = reminderMapper.toResponse(updatedReminder);
        return ResponseEntity.ok(new Response<>(response, "Reminder updated successfully"));
    }

}
