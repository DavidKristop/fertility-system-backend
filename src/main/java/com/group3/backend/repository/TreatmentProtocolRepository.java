package com.group3.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.TreatmentProtocol;

@Repository
public interface TreatmentProtocolRepository extends JpaRepository<TreatmentProtocol,UUID>{
}