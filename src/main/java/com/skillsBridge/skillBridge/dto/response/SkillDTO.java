package com.skillsBridge.skillBridge.dto.response;

import com.skillsBridge.skillBridge.enums.SkillType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private Integer popularityScore;
    private Integer proficiencyLevel;  // For user skills
    private SkillType skillType;  // OFFERING or SEEKING
}
