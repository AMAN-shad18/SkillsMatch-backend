package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.RefreshToken;
import com.skillsBridge.skillBridge.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(Users user) ;
    void deleteByToken(String token) ;
}
