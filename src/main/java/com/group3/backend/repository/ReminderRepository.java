package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    Page<Reminder> findBySendToId(UUID userId, Pageable pageable);
}
