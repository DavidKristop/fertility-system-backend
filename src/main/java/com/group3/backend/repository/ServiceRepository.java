package com.group3.backend.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Page<Service> findByNameIgnoreCaseContainingAndIsActive(@Param("name") String name, @Param("isActive") boolean isActive, Pageable pageable);
    boolean existsByName(String name);
}
