package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Page<Service> findByNameIgnoreCaseContainingAndIsActiveAndIdNot(@Param("name") String name, @Param("isActive") boolean isActive, @Param("consultationServiceId") UUID consultationServiceId, Pageable pageable);
    List<Service> findByIdIn(List<UUID> ids);
    boolean existsByName(String name);
}
