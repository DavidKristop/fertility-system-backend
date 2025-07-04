package com.group3.backend.repository;

import com.group3.backend.model.Payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
        List<Payment> findByStatusAndPaymentDeadlineLessThan(
                @Param("status") Payment.Status status,
                @Param("deadline") LocalDateTime deadline);

        Page<Payment> findByUserIdAndStatusIn(UUID userId, List<Payment.Status> status, Pageable pageable);

        Page<Payment> findByUserEmailIgnoreCaseContainingAndStatusIn(String email, List<Payment.Status> status, Pageable pageable);

        Payment findByIdAndUserId(UUID id, UUID userId);
}
