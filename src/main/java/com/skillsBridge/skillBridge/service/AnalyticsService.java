package com.skillsBridge.skillBridge.service;

import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.ExchangeTrendDTO;
import com.skillsBridge.skillBridge.dto.response.SkillDemandDTO;
import com.skillsBridge.skillBridge.dto.response.UserStatsDTO;
import com.skillsBridge.skillBridge.entity.Skill;
import com.skillsBridge.skillBridge.entity.UserSkill;
import com.skillsBridge.skillBridge.entity.Users;
import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import com.skillsBridge.skillBridge.enums.SkillType;
import com.skillsBridge.skillBridge.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Get skill demand - skills with high demand but low supply
     */
    public ApiResponse<List<SkillDemandDTO>> getSkillsDemand() {
        try {
            List<Skill> allSkills = skillRepository.findAll();

            List<SkillDemandDTO> demandList = allSkills.stream()
                    .map(skill -> {
                        List<UserSkill> seeking = userSkillRepository.findAll().stream()
                                .filter(us -> us.getSkill().getId().equals(skill.getId())
                                        && us.getSkillType() == SkillType.SEEKING)
                                .collect(Collectors.toList());

                        List<UserSkill> offering = userSkillRepository.findAll().stream()
                                .filter(us -> us.getSkill().getId().equals(skill.getId())
                                        && us.getSkillType() == SkillType.OFFERING)
                                .collect(Collectors.toList());

                        long demandCount = seeking.size();
                        long offeringCount = offering.size();
                        double demandRatio = offeringCount > 0 ? (double) demandCount / offeringCount : demandCount;

                        return SkillDemandDTO.builder()
                                .skillName(skill.getName())
                                .demandCount(demandCount)
                                .offeringCount(offeringCount)
                                .popularityScore(skill.getPopularityScore())
                                .demandRatio(Math.round(demandRatio * 100.0) / 100.0)
                                .build();
                    })
                    .filter(dto -> dto.getDemandCount() > 0)  // Only skills with demand
                    .sorted((a, b) -> Double.compare(b.getDemandRatio(), a.getDemandRatio()))
                    .collect(Collectors.toList());

            return ApiResponse.success("Skills demand retrieved successfully", demandList);

        } catch (Exception e) {
            log.error("Error retrieving skills demand: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve skills demand");
        }
    }

    /**
     * Get user statistics
     */
    public ApiResponse<UserStatsDTO> getUserStats() {
        try {
            List<Users> allUsers = userRepository.findAll();
            long totalUsers = allUsers.size();
            long verifiedUsers = allUsers.stream()
                    .filter(Users::getIsVerified)
                    .count();

            List<UserSkill> allUserSkills = userSkillRepository.findAll();
            long usersWithSkills = allUserSkills.stream()
                    .map(UserSkill::getUser)
                    .distinct()
                    .count();

            // Users with exchanges in last 30 days
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long activeUsers = exchangeRepository.findAll().stream()
                    .filter(e -> e.getCreatedAt().isAfter(thirtyDaysAgo))
                    .flatMap(e -> java.util.stream.Stream.of(e.getRequester(), e.getProvider()))
                    .distinct()
                    .count();

            double avgSkillsPerUser = usersWithSkills > 0 
                    ? (double) allUserSkills.size() / usersWithSkills 
                    : 0;

            // Calculate average rating across all users
            List<Users> usersWithReviews = allUsers.stream()
                    .filter(u -> reviewRepository.countReviewsForUser(u) > 0)
                    .collect(Collectors.toList());

            double avgRating = 0;
            if (!usersWithReviews.isEmpty()) {
                avgRating = usersWithReviews.stream()
                        .mapToDouble(u -> {
                            Double rating = reviewRepository.getAverageRatingForUser(u);
                            return rating != null ? rating : 0;
                        })
                        .average()
                        .orElse(0);
            }

            UserStatsDTO stats = UserStatsDTO.builder()
                    .totalUsers(totalUsers)
                    .verifiedUsers(verifiedUsers)
                    .usersWithSkills(usersWithSkills)
                    .activeUsers(activeUsers)
                    .averageSkillsPerUser(Math.round(avgSkillsPerUser * 100.0) / 100.0)
                    .averageRating(Math.round(avgRating * 100.0) / 100.0)
                    .build();

            return ApiResponse.success("User statistics retrieved successfully", stats);

        } catch (Exception e) {
            log.error("Error retrieving user statistics: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve user statistics");
        }
    }

    /**
     * Get exchange trends - status distribution
     */
    public ApiResponse<List<ExchangeTrendDTO>> getExchangesTrend() {
        try {
            List<Object> allExchanges = new ArrayList<>(exchangeRepository.findAll());
            long totalExchanges = allExchanges.size();

            if (totalExchanges == 0) {
                return ApiResponse.success("No exchanges found", new ArrayList<>());
            }

            Map<ExchangeStatus, Long> exchangesByStatus = exchangeRepository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getStatus(),
                            Collectors.counting()
                    ));

            List<ExchangeTrendDTO> trends = exchangesByStatus.entrySet().stream()
                    .map(entry -> {
                        long count = entry.getValue();
                        double percentage = (double) count / totalExchanges * 100;
                        return ExchangeTrendDTO.builder()
                                .status(entry.getKey())
                                .count(count)
                                .percentage(Math.round(percentage * 100.0) / 100.0)
                                .build();
                    })
                    .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                    .collect(Collectors.toList());

            return ApiResponse.success("Exchange trends retrieved successfully", trends);

        } catch (Exception e) {
            log.error("Error retrieving exchange trends: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve exchange trends");
        }
    }
}
