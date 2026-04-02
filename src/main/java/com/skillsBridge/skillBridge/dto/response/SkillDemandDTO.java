package com.skillsBridge.skillBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDemandDTO {
    private String skillName;
    private Long demandCount;
    private Long offeringCount;
    private Integer popularityScore;
    private Double demandRatio;  // demandCount / offeringCount
}
