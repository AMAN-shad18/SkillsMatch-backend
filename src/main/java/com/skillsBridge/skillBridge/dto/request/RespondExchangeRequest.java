package com.skillsBridge.skillBridge.dto.request;

import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RespondExchangeRequest {
    @NotNull(message = "Status is required (ACCEPTED, REJECTED, COMPLETED)")
    private ExchangeStatus status;

    private String sessionLink;
}
