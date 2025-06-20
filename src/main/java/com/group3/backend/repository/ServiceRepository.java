package com.group3.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group3.backend.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    List<Service> findByIsActiveTrue();
}
