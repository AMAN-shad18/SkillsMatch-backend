package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.PasswordResetToken;
import com.skillsBridge.skillBridge.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(Users user);
    void deleteByUser(Users user);
}
