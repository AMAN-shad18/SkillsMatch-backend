package com.skillsBridge.skillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String rollNumber;
    private String email;
    private String department;
    private Integer semester;
    private String bio;
    private String profilePicUrl;
    private Boolean isVerified;
    private Double averageRating;
    private Long reviewCount;
    private Long completedExchanges;
    private List<SkillDTO> offeredSkills;
    private List<SkillDTO> seekingSkills;
    private LocalDateTime createdAt;
}
