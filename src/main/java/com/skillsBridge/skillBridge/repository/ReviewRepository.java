package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.Review;
import com.skillsBridge.skillBridge.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewee(Users reviewee);
    List<Review> findByReviewer(Users reviewer);
    Optional<Review> findByExchangeId(Long exchangeId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :user")
    Double getAverageRatingForUser(@Param("user") Users user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee = :user")
    Long countReviewsForUser(@Param("user") Users user);
}
