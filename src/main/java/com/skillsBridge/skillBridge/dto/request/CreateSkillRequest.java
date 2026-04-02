package com.skillsBridge.skillBridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSkillRequest {
    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String name;

    private String category;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
