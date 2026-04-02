package com.skillsBridge.skillBridge.dto.response;

import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeDTO {
    private Long id;
    private UserResponse requester;
    private UserResponse provider;
    private SkillDTO skillOffered;
    private SkillDTO skillRequested;
    private ExchangeStatus status;
    private LocalDateTime sessionDate;
    private String sessionLink;
    private Integer rating;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
