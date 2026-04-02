package com.skillsBridge.skillBridge.controller;

import com.skillsBridge.skillBridge.dto.request.UpdateProfileRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.UserProfileResponse;
import com.skillsBridge.skillBridge.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * Handles user profile and search endpoints
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user's profile
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        log.info("Getting current user profile");
        try {
            ApiResponse<UserProfileResponse> response = userService.getCurrentUserProfile();
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error getting profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve profile"));
        }
    }

    /**
     * Update user profile
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating user profile");
        try {
            ApiResponse<UserProfileResponse> response = userService.updateProfile(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update profile"));
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        log.info("Getting user profile for ID: {}", id);
        try {
            ApiResponse<UserProfileResponse> response = userService.getUserById(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error getting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user"));
        }
    }

    /**
     * Search users
     * GET /api/users/search?q=query&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> searchUsers(
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Searching users with query: {}", query);
        try {
            if (query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Search query cannot be empty"));
            }

            Pageable pageable = PageRequest.of(page, size);
            ApiResponse<Page<UserProfileResponse>> response = userService.searchUsers(query, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search users"));
        }
    }

    /**
     * Find matching users with complementary skills
     * GET /api/users/matches?page=0&size=10
     */
    @GetMapping("/matches")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> findMatches(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Finding skill matches for current user");
        try {
            Pageable pageable = PageRequest.of(page, size);
            ApiResponse<Page<UserProfileResponse>> response = userService.findMatches(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error finding matches: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to find matches"));
        }
    }
}
