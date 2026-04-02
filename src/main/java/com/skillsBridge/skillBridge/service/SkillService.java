package com.skillsBridge.skillBridge.service;

import com.skillsBridge.skillBridge.dto.request.CreateSkillRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.SkillDTO;
import com.skillsBridge.skillBridge.entity.Skill;
import com.skillsBridge.skillBridge.entity.UserSkill;
import com.skillsBridge.skillBridge.entity.Users;
import com.skillsBridge.skillBridge.repository.SkillRepository;
import com.skillsBridge.skillBridge.repository.UserRepository;
import com.skillsBridge.skillBridge.repository.UserSkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    /**
     * Get all skills with pagination
     */
    public ApiResponse<Page<SkillDTO>> getAllSkills(Pageable pageable) {
        try {
            Page<Skill> skills = skillRepository.findAll(pageable);
            Page<SkillDTO> skillDTOs = skills.map(skill -> SkillDTO.builder()
                    .id(skill.getId())
                    .name(skill.getName())
                    .category(skill.getCategory())
                    .description(skill.getDescription())
                    .popularityScore(skill.getPopularityScore())
                    .build());

            return ApiResponse.success("Skills retrieved successfully", skillDTOs);
        } catch (Exception e) {
            log.error("Error retrieving skills: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve skills");
        }
    }

    /**
     * Create a new skill (admin only)
     */
    public ApiResponse<SkillDTO> createSkill(CreateSkillRequest request) {
        try {
            // Check if skill already exists
            if (skillRepository.findByName(request.getName()).isPresent()) {
                return ApiResponse.error("Skill with this name already exists");
            }

            Skill skill = Skill.builder()
                    .name(request.getName())
                    .category(request.getCategory())
                    .description(request.getDescription())
                    .popularityScore(0)
                    .build();

            Skill savedSkill = skillRepository.save(skill);

            SkillDTO skillDTO = SkillDTO.builder()
                    .id(savedSkill.getId())
                    .name(savedSkill.getName())
                    .category(savedSkill.getCategory())
                    .description(savedSkill.getDescription())
                    .popularityScore(savedSkill.getPopularityScore())
                    .build();

            return ApiResponse.success("Skill created successfully", skillDTO);
        } catch (Exception e) {
            log.error("Error creating skill: {}", e.getMessage());
            return ApiResponse.error("Failed to create skill");
        }
    }

    /**
     * Get all skill categories
     */
    public ApiResponse<List<String>> getCategories() {
        try {
            List<String> categories = skillRepository.findAllCategories();
            return ApiResponse.success("Categories retrieved successfully", categories);
        } catch (Exception e) {
            log.error("Error retrieving categories: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve categories");
        }
    }

    /**
     * Get most popular skills
     */
    public ApiResponse<List<SkillDTO>> getPopularSkills() {
        try {
            List<Skill> popularSkills = skillRepository.findTopPopularSkills();
            List<SkillDTO> skillDTOs = popularSkills.stream()
                    .map(skill -> SkillDTO.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .category(skill.getCategory())
                            .description(skill.getDescription())
                            .popularityScore(skill.getPopularityScore())
                            .build())
                    .collect(Collectors.toList());

            return ApiResponse.success("Popular skills retrieved successfully", skillDTOs);
        } catch (Exception e) {
            log.error("Error retrieving popular skills: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve popular skills");
        }
    }

    /**
     * Get skills by category
     */
    public ApiResponse<List<SkillDTO>> getSkillsByCategory(String category) {
        try {
            List<Skill> skills = skillRepository.findByCategory(category);
            List<SkillDTO> skillDTOs = skills.stream()
                    .map(skill -> SkillDTO.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .category(skill.getCategory())
                            .description(skill.getDescription())
                            .popularityScore(skill.getPopularityScore())
                            .build())
                    .collect(Collectors.toList());

            return ApiResponse.success("Skills retrieved by category", skillDTOs);
        } catch (Exception e) {
            log.error("Error retrieving skills by category: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve skills");
        }
    }

    /**
     * Search skills by name
     */
    public ApiResponse<List<SkillDTO>> searchSkills(String query) {
        try {
            List<Skill> skills = skillRepository.findByNameContainingIgnoreCase(query);
            List<SkillDTO> skillDTOs = skills.stream()
                    .map(skill -> SkillDTO.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .category(skill.getCategory())
                            .description(skill.getDescription())
                            .popularityScore(skill.getPopularityScore())
                            .build())
                    .collect(Collectors.toList());

            return ApiResponse.success("Skills found", skillDTOs);
        } catch (Exception e) {
            log.error("Error searching skills: {}", e.getMessage());
            return ApiResponse.error("Failed to search skills");
        }
    }
}
