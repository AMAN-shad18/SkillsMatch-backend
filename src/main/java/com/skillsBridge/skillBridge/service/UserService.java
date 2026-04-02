package com.skillsBridge.skillBridge.service;

import com.skillsBridge.skillBridge.dto.request.UpdateProfileRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.SkillDTO;
import com.skillsBridge.skillBridge.dto.response.UserProfileResponse;
import com.skillsBridge.skillBridge.entity.UserSkill;
import com.skillsBridge.skillBridge.entity.Users;
import com.skillsBridge.skillBridge.enums.SkillType;
import com.skillsBridge.skillBridge.repository.ReviewRepository;
import com.skillsBridge.skillBridge.repository.UserRepository;
import com.skillsBridge.skillBridge.repository.UserSkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Get current user's profile
     */
    public ApiResponse<UserProfileResponse> getCurrentUserProfile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ApiResponse.error("User not found");
            }

            return ApiResponse.success("Profile retrieved successfully", buildUserProfileResponse(user));
        } catch (Exception e) {
            log.error("Error retrieving current user profile: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve profile");
        }
    }

    /**
     * Update user profile
     */
    public ApiResponse<UserProfileResponse> updateProfile(UpdateProfileRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ApiResponse.error("User not found");
            }

            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getDepartment() != null) {
                user.setDepartment(request.getDepartment());
            }
            if (request.getSemester() != null) {
                user.setSemester(request.getSemester());
            }
            if (request.getBio() != null) {
                user.setBio(request.getBio());
            }
            if (request.getProfilePicUrl() != null) {
                user.setProfilePicUrl(request.getProfilePicUrl());
            }

            Users updatedUser = userRepository.save(user);
            return ApiResponse.success("Profile updated successfully", buildUserProfileResponse(updatedUser));
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ApiResponse.error("Failed to update profile");
        }
    }

    /**
     * Get user by ID
     */
    public ApiResponse<UserProfileResponse> getUserById(Long userId) {
        try {
            Users user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ApiResponse.error("User not found");
            }

            return ApiResponse.success("User retrieved successfully", buildUserProfileResponse(user));
        } catch (Exception e) {
            log.error("Error retrieving user by ID: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve user");
        }
    }

    /**
     * Search users by name, email, or roll number
     */
    public ApiResponse<Page<UserProfileResponse>> searchUsers(String query, Pageable pageable) {
        try {
            List<Users> users = userRepository.findAll().stream()
                    .filter(u -> u.getFullName().toLowerCase().contains(query.toLowerCase())
                            || u.getEmail().toLowerCase().contains(query.toLowerCase())
                            || u.getRollNumber().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), users.size());
            List<UserProfileResponse> pageContent = users.subList(start, end)
                    .stream()
                    .map(this::buildUserProfileResponse)
                    .collect(Collectors.toList());

            Page<UserProfileResponse> page = new PageImpl<>(pageContent, pageable, users.size());
            return ApiResponse.success("Users found", page);
        } catch (Exception e) {
            log.error("Error searching users: {}", e.getMessage());
            return ApiResponse.error("Failed to search users");
        }
    }

    /**
     * Find matching skills - users offering skills you want to learn
     */
    public ApiResponse<Page<UserProfileResponse>> findMatches(Pageable pageable) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users currentUser = userRepository.findByEmail(email).orElse(null);

            if (currentUser == null) {
                return ApiResponse.error("User not found");
            }

            // Get skills the current user is seeking
            List<UserSkill> seekingSkills = userSkillRepository
                    .findByUserAndSkillType(currentUser, SkillType.SEEKING);

            if (seekingSkills.isEmpty()) {
                return ApiResponse.success("No skills to match", Page.empty(pageable));
            }

            // Find users offering those skills
            List<Users> matchedUsers = userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(currentUser.getId())) // Exclude self
                    .filter(u -> {
                        List<UserSkill> offeringSkills = userSkillRepository
                                .findByUserAndSkillType(u, SkillType.OFFERING);
                        return offeringSkills.stream()
                                .anyMatch(offered -> seekingSkills.stream()
                                        .anyMatch(seeking -> seeking.getSkill().getId()
                                                .equals(offered.getSkill().getId())));
                    })
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), matchedUsers.size());
            List<UserProfileResponse> pageContent = matchedUsers.subList(start, end)
                    .stream()
                    .map(this::buildUserProfileResponse)
                    .collect(Collectors.toList());

            Page<UserProfileResponse> page = new PageImpl<>(pageContent, pageable, matchedUsers.size());
            return ApiResponse.success("Matches found", page);
        } catch (Exception e) {
            log.error("Error finding matches: {}", e.getMessage());
            return ApiResponse.error("Failed to find matches");
        }
    }

    /**
     * Build UserProfileResponse from Users entity
     */
    private UserProfileResponse buildUserProfileResponse(Users user) {
        List<UserSkill> allUserSkills = userSkillRepository.findByUser(user);

        List<SkillDTO> offeredSkills = allUserSkills.stream()
                .filter(us -> us.getSkillType() == SkillType.OFFERING)
                .map(us -> SkillDTO.builder()
                        .id(us.getSkill().getId())
                        .name(us.getSkill().getName())
                        .category(us.getSkill().getCategory())
                        .description(us.getSkill().getDescription())
                        .proficiencyLevel(us.getProficiencyLevel())
                        .skillType(us.getSkillType())
                        .build())
                .collect(Collectors.toList());

        List<SkillDTO> seekingSkills = allUserSkills.stream()
                .filter(us -> us.getSkillType() == SkillType.SEEKING)
                .map(us -> SkillDTO.builder()
                        .id(us.getSkill().getId())
                        .name(us.getSkill().getName())
                        .category(us.getSkill().getCategory())
                        .description(us.getSkill().getDescription())
                        .proficiencyLevel(us.getProficiencyLevel())
                        .skillType(us.getSkillType())
                        .build())
                .collect(Collectors.toList());

        Double avgRating = reviewRepository.getAverageRatingForUser(user);
        Long reviewCount = reviewRepository.countReviewsForUser(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .rollNumber(user.getRollNumber())
                .email(user.getEmail())
                .department(user.getDepartment())
                .semester(user.getSemester())
                .bio(user.getBio())
                .profilePicUrl(user.getProfilePicUrl())
                .isVerified(user.getIsVerified())
                .averageRating(avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0)
                .reviewCount(reviewCount != null ? reviewCount : 0L)
                .completedExchanges(0L)  // Will be calculated separately
                .offeredSkills(offeredSkills)
                .seekingSkills(seekingSkills)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
