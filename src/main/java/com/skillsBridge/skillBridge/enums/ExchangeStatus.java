package com.skillsBridge.skillBridge.enums;

public enum ExchangeStatus {

    PENDING,      // Waiting for provider to accept
    ACCEPTED,     // Provider accepted, session scheduled
    IN_PROGRESS,  // Session is ongoing
    COMPLETED,    // Session completed successfully
    CANCELLED,    // Exchange was cancelled
    REJECTED
}
