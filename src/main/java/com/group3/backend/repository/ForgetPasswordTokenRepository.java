package com.group3.backend.repository;

import com.group3.backend.model.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ForgetPasswordTokenRepository extends JpaRepository<ForgetPasswordToken, UUID> {
    Optional<ForgetPasswordToken> findByToken(String token);
}
