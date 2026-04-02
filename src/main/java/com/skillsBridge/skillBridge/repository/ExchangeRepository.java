package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.Exchange;
import com.skillsBridge.skillBridge.entity.Users;
import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findByRequesterAndStatus(Users requester, ExchangeStatus status);
    List<Exchange> findByProviderAndStatus(Users provider, ExchangeStatus status);
    List<Exchange> findByStatusAndCreatedAtBetween(ExchangeStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e FROM Exchange e WHERE (e.requester = :user OR e.provider = :user) AND e.status IN ('COMPLETED', 'CANCELLED')")
    List<Exchange> findHistoryByUser(@Param("user") Users user);
    
    @Query("SELECT e FROM Exchange e WHERE (e.requester = :user OR e.provider = :user) ORDER BY e.createdAt DESC")
    List<Exchange> findAllByUser(@Param("user") Users user);
    
    @Query("SELECT COUNT(e) FROM Exchange e WHERE e.status = 'COMPLETED' AND (e.requester = :user OR e.provider = :user)")
    Long countCompletedExchanges(@Param("user") Users user);
}
