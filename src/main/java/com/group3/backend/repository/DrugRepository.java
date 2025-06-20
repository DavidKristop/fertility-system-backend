package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.Drug;

@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID> {

    List<Drug> findByIsActiveTrue();
}
