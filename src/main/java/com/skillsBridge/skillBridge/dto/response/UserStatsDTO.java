package com.skillsBridge.skillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDTO {
    private Long totalUsers;
    private Long verifiedUsers;
    private Long usersWithSkills;
    private Long activeUsers;  // Users with recent exchanges
    private Double averageSkillsPerUser;
    private Double averageRating;
}
