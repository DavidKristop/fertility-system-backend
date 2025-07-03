package com.group3.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.Reminder;
import com.group3.backend.repository.ReminderRepository;

@Service
public class ReminderService {
    
    @Autowired
    ReminderRepository reminderRepository;

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
}
