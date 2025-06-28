package com.group3.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import com.group3.backend.model.Drug;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {

    @Query("SELECT d FROM Drug d WHERE (LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND d.isActive = :isActive")
    Page<Drug> searchDrugs(String name, boolean isActive, Pageable pageable);

    boolean existsByName(String name);
}
