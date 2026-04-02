package com.skillsBridge.skillBridge.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateExchangeRequest {
    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotNull(message = "Skill offered ID is required")
    private Long skillOfferedId;

    @NotNull(message = "Skill requested ID is required")
    private Long skillRequestedId;
}
