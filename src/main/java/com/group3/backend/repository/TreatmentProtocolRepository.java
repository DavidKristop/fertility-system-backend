package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.TreatmentProtocol;

@Repository
public interface TreatmentProtocolRepository extends JpaRepository<TreatmentProtocol, UUID> {

    List<TreatmentProtocol> findByIdIn(@Param("ids") List<UUID> ids);

    Page<TreatmentProtocol> findByTitleIgnoreCaseContainingAndIsActive(
            @Param("title") String title,
            @Param("isActive") boolean isActive,
            Pageable pageable);

    boolean existsByTitle(String title);
}