package com.skillsBridge.skillBridge.controller;

import com.skillsBridge.skillBridge.dto.request.CreateSkillRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.SkillDTO;
import com.skillsBridge.skillBridge.service.SkillService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Skill Controller
 * Handles skill retrieval and management endpoints
 */
@RestController
@RequestMapping("/api/skills")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * Get all skills with pagination
     * GET /api/skills?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SkillDTO>>> getAllSkills(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Getting all skills - page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            ApiResponse<Page<SkillDTO>> response = skillService.getAllSkills(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve skills"));
        }
    }

    /**
     * Create a new skill
     * POST /api/skills
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillDTO>> createSkill(@Valid @RequestBody CreateSkillRequest request) {
        log.info("Creating new skill: {}", request.getName());
        try {
            ApiResponse<SkillDTO> response = skillService.createSkill(request);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error creating skill: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create skill"));
        }
    }

    /**
     * Get all skill categories
     * GET /api/skills/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        log.info("Getting all skill categories");
        try {
            ApiResponse<List<String>> response = skillService.getCategories();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve categories"));
        }
    }

    /**
     * Get most popular skills
     * GET /api/skills/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<SkillDTO>>> getPopularSkills() {
        log.info("Getting popular skills");
        try {
            ApiResponse<List<SkillDTO>> response = skillService.getPopularSkills();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting popular skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve popular skills"));
        }
    }
}
