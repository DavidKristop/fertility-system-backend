package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.AssignDrug;

@Repository
public interface AssignDrugRepository extends JpaRepository<AssignDrug, UUID> {
    List<AssignDrug> findByIdIn(List<UUID> ids);
}
