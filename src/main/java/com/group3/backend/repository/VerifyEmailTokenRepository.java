package com.group3.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.model.User;
import com.group3.backend.model.VerifyEmailToken;

public interface VerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken, UUID> {
    void deleteByUser(User user);

    Optional<VerifyEmailToken> findByToken(String token);

    boolean existsByToken(String token);
}
