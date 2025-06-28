package com.group3.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import com.group3.backend.model.Drug;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {

    Page<Drug> findByNameContainingAndIsActive(String name, boolean isActive, Pageable pageable);

    boolean existsByName(String name);
}
