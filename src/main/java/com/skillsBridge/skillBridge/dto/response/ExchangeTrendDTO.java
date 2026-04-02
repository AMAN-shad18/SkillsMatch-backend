package com.skillsBridge.skillBridge.dto.response;

import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeTrendDTO {
    private ExchangeStatus status;
    private Long count;
    private Double percentage;
}
