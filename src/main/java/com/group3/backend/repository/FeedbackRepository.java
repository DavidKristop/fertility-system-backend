package com.group3.backend.repository;

import com.group3.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findAllByOrderByIdDesc();
}
