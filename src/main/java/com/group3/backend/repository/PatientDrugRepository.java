package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.Drug;
import com.group3.backend.model.PatientDrug;

public interface PatientDrugRepository extends JpaRepository<PatientDrug, UUID> {
    List<PatientDrug> findByIdIn(List<UUID> ids);
}
