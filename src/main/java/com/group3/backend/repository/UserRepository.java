package com.group3.backend.repository;

import com.group3.backend.constants.Roles;
import com.group3.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
    List<User> findAllByRoleName(String roleName);

    Page<User> findAllByRoleNameAndEmailIgnoreCaseContainingAndIsActive(Roles roleName, String email, boolean isActive, Pageable pageable);

    Page<User> findAllByRoleNameAndFullNameIgnoreCaseContainingAndIsActive(Roles roleName, String fullName, boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) AND u.email <> :excludeEmail")
    Page<User> findByEmailExcludingCurrent(String email, String excludeEmail, Pageable pageable);

    @Query("""
        SELECT u FROM User u 
        WHERE u.role.name = :role 
        AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) 
        AND u.email <> :excludeEmail
    """)
    Page<User> findByRoleNameAndEmailExcludingCurrent(
        @Param("role") Roles role, 
        @Param("email") String email, 
        @Param("excludeEmail") String excludeEmail, 
        Pageable pageable
    );
}
