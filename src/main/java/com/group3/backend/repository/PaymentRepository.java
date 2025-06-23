package com.group3.backend.repository;

import com.group3.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("SELECT p FROM Payment p " +
            "WHERE p.status = :status AND " +
            "p.paymentDeadline < :deadline")
    List<Payment> findByStatusAndPaymentDeadlineLessThan(
            @Param("status") Payment.Status status,
            @Param("deadline") Timestamp deadline);
}
