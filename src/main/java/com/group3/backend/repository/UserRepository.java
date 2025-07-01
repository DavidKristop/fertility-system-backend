package com.group3.backend.repository;

import com.group3.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
    List<User> findAllByRoleName(String roleName);

    Page<User> findAllByRoleNameAndFullNameIgnoreCaseContainingAndIsActive(String roleName, String fullName, boolean isActive, Pageable pageable);
}
