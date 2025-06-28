package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.TreatmentProtocol;

@Repository
public interface TreatmentProtocolRepository extends JpaRepository<TreatmentProtocol, UUID> {

    List<TreatmentProtocol> findByIdIn(List<UUID> ids);

    @NonNull
    Page<TreatmentProtocol> findByTitleContainingAndIsActive(@Param("title") String title, @Param("isActive") boolean isActive, @NonNull Pageable pageable);

    boolean existsByTitle(String title);
}