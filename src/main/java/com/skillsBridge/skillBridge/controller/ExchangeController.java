package com.skillsBridge.skillBridge.controller;

import com.skillsBridge.skillBridge.dto.request.CreateExchangeRequest;
import com.skillsBridge.skillBridge.dto.request.RateExchangeRequest;
import com.skillsBridge.skillBridge.dto.request.RespondExchangeRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.ExchangeDTO;
import com.skillsBridge.skillBridge.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Exchange Controller
 * Handles skill exchange requests and responses
 */
@RestController
@RequestMapping("/api/exchanges")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    /**
     * Create a new exchange request
     * POST /api/exchanges/request
     */
    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ExchangeDTO>> createExchangeRequest(
            @Valid @RequestBody CreateExchangeRequest request) {
        log.info("Creating exchange request");
        try {
            ApiResponse<ExchangeDTO> response = exchangeService.createExchangeRequest(request);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error creating exchange request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create exchange request"));
        }
    }

    /**
     * Get pending exchanges for current user
     * GET /api/exchanges/pending?page=0&size=10
     */
    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ExchangeDTO>>> getPendingExchanges(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Getting pending exchanges");
        try {
            Pageable pageable = PageRequest.of(page, size);
            ApiResponse<Page<ExchangeDTO>> response = exchangeService.getPendingExchanges(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting pending exchanges: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve pending exchanges"));
        }
    }

    /**
     * Respond to an exchange request (accept/reject/complete)
     * PUT /api/exchanges/{id}/respond
     */
    @PutMapping("/{id}/respond")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ExchangeDTO>> respondToExchange(
            @PathVariable Long id,
            @Valid @RequestBody RespondExchangeRequest request) {
        log.info("Responding to exchange request: {}", id);
        try {
            ApiResponse<ExchangeDTO> response = exchangeService.respondToExchange(id, request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error responding to exchange: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to respond to exchange"));
        }
    }

    /**
     * Get exchange history for current user
     * GET /api/exchanges/history?page=0&size=10
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ExchangeDTO>>> getExchangeHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Getting exchange history");
        try {
            Pageable pageable = PageRequest.of(page, size);
            ApiResponse<Page<ExchangeDTO>> response = exchangeService.getExchangeHistory(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting exchange history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve exchange history"));
        }
    }

    /**
     * Rate an exchange (only after completion)
     * POST /api/exchanges/{id}/rate
     */
    @PostMapping("/{id}/rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ExchangeDTO>> rateExchange(
            @PathVariable Long id,
            @Valid @RequestBody RateExchangeRequest request) {
        log.info("Rating exchange: {}", id);
        try {
            ApiResponse<ExchangeDTO> response = exchangeService.rateExchange(id, request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Error rating exchange: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to rate exchange"));
        }
    }
}
