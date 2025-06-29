package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.TreatmentProtocol;

@Repository
public interface TreatmentProtocolRepository extends JpaRepository<TreatmentProtocol, UUID> {

    @Query("SELECT DISTINCT tp FROM TreatmentProtocol tp " +
        "JOIN tp.phases phase " +
        "JOIN phase.drugs drug " +
        "JOIN phase.services service " +
        "WHERE tp.id IN :ids AND tp.isActive = true " +
        "AND drug.drug.isActive = true " +
        "AND service.service.isActive = true")
    List<TreatmentProtocol> findByIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT DISTINCT tp FROM TreatmentProtocol tp " +
        "JOIN tp.phases phase " +
        "JOIN phase.drugs drug " +
        "JOIN phase.services service " +
        "WHERE LOWER(tp.title) LIKE CONCAT('%', LOWER(:title), '%') AND tp.isActive = true " +
        "AND drug.drug.isActive = true " +
        "AND service.service.isActive = true")
    Page<TreatmentProtocol> findByTitleIgnoreCaseContainingAndIsActive(
            @Param("title") String title,
            @Param("isActive") boolean isActive,
            Pageable pageable);

    boolean existsByTitle(String title);
}