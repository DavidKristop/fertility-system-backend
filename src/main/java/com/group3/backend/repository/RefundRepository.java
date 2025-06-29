package com.group3.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Refund;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    
}
