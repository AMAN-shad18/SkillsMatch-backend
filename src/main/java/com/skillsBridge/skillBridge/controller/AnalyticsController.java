package com.skillsBridge.skillBridge.controller;

import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.ExchangeTrendDTO;
import com.skillsBridge.skillBridge.dto.response.SkillDemandDTO;
import com.skillsBridge.skillBridge.dto.response.UserStatsDTO;
import com.skillsBridge.skillBridge.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Analytics Controller
 * Provides analytics and insights about the platform
 */
@RestController
@RequestMapping("/api/analytics")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get skill demand analysis - skills with high demand vs supply
     * GET /api/analytics/skills-demand
     */
    @GetMapping("/skills-demand")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SkillDemandDTO>>> getSkillsDemand() {
        log.info("Getting skills demand analytics");
        try {
            ApiResponse<List<SkillDemandDTO>> response = analyticsService.getSkillsDemand();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting skills demand: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve skills demand analytics"));
        }
    }

    /**
     * Get user statistics - total users, verified users, active users, etc.
     * GET /api/analytics/user-stats
     */
    @GetMapping("/user-stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserStatsDTO>> getUserStats() {
        log.info("Getting user statistics analytics");
        try {
            ApiResponse<UserStatsDTO> response = analyticsService.getUserStats();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user statistics"));
        }
    }

    /**
     * Get exchange trends - distribution of exchange statuses
     * GET /api/analytics/exchanges-trend
     */
    @GetMapping("/exchanges-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ExchangeTrendDTO>>> getExchangesTrend() {
        log.info("Getting exchanges trend analytics");
        try {
            ApiResponse<List<ExchangeTrendDTO>> response = analyticsService.getExchangesTrend();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting exchanges trend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve exchanges trend analytics"));
        }
    }
}
