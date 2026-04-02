package com.skillsBridge.skillBridge.dto.request;

import com.skillsBridge.skillBridge.enums.SkillType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserSkillRequest {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Skill type is required (OFFERING or SEEKING)")
    private SkillType skillType;

    @Min(value = 1, message = "Proficiency level must be between 1 and 5")
    private Integer proficiencyLevel = 1;
}
