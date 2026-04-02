package com.skillsBridge.skillBridge.service;

import com.skillsBridge.skillBridge.dto.request.CreateExchangeRequest;
import com.skillsBridge.skillBridge.dto.request.RateExchangeRequest;
import com.skillsBridge.skillBridge.dto.request.RespondExchangeRequest;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.ExchangeDTO;
import com.skillsBridge.skillBridge.dto.response.SkillDTO;
import com.skillsBridge.skillBridge.dto.response.UserResponse;
import com.skillsBridge.skillBridge.entity.*;
import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import com.skillsBridge.skillBridge.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Create a new exchange request
     */
    public ApiResponse<ExchangeDTO> createExchangeRequest(CreateExchangeRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users requester = userRepository.findByEmail(email).orElse(null);

            if (requester == null) {
                return ApiResponse.error("User not found");
            }

            Users provider = userRepository.findById(request.getProviderId()).orElse(null);
            if (provider == null) {
                return ApiResponse.error("Provider not found");
            }

            Skill skillOffered = skillRepository.findById(request.getSkillOfferedId()).orElse(null);
            if (skillOffered == null) {
                return ApiResponse.error("Offered skill not found");
            }

            Skill skillRequested = skillRepository.findById(request.getSkillRequestedId()).orElse(null);
            if (skillRequested == null) {
                return ApiResponse.error("Requested skill not found");
            }

            Exchange exchange = Exchange.builder()
                    .requester(requester)
                    .provider(provider)
                    .skillOffered(skillOffered)
                    .skillRequested(skillRequested)
                    .status(ExchangeStatus.PENDING)
                    .build();

            Exchange savedExchange = exchangeRepository.save(exchange);
            return ApiResponse.success("Exchange request created successfully", buildExchangeDTO(savedExchange));

        } catch (Exception e) {
            log.error("Error creating exchange request: {}", e.getMessage());
            return ApiResponse.error("Failed to create exchange request");
        }
    }

    /**
     * Get pending exchanges for current user
     */
    public ApiResponse<Page<ExchangeDTO>> getPendingExchanges(Pageable pageable) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ApiResponse.error("User not found");
            }

            // Find pending exchanges where user is provider
            List<Exchange> pending = exchangeRepository.findByProviderAndStatus(user, ExchangeStatus.PENDING);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), pending.size());
            List<ExchangeDTO> pageContent = pending.subList(start, end)
                    .stream()
                    .map(this::buildExchangeDTO)
                    .collect(Collectors.toList());

            Page<ExchangeDTO> page = new PageImpl<>(pageContent, pageable, pending.size());
            return ApiResponse.success("Pending exchanges retrieved", page);

        } catch (Exception e) {
            log.error("Error retrieving pending exchanges: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve pending exchanges");
        }
    }

    /**
     * Respond to an exchange request
     */
    public ApiResponse<ExchangeDTO> respondToExchange(Long exchangeId, RespondExchangeRequest request) {
        try {
            Exchange exchange = exchangeRepository.findById(exchangeId).orElse(null);

            if (exchange == null) {
                return ApiResponse.error("Exchange not found");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users provider = userRepository.findByEmail(email).orElse(null);

            if (provider == null || !provider.getId().equals(exchange.getProvider().getId())) {
                return ApiResponse.error("You are not authorized to respond to this exchange");
            }

            exchange.setStatus(request.getStatus());
            if (request.getSessionLink() != null) {
                exchange.setSessionLink(request.getSessionLink());
            }

            if (request.getStatus() == ExchangeStatus.COMPLETED) {
                exchange.setCompletedAt(LocalDateTime.now());
            }

            Exchange updatedExchange = exchangeRepository.save(exchange);
            return ApiResponse.success("Exchange response submitted", buildExchangeDTO(updatedExchange));

        } catch (Exception e) {
            log.error("Error responding to exchange: {}", e.getMessage());
            return ApiResponse.error("Failed to respond to exchange");
        }
    }

    /**
     * Get exchange history for current user
     */
    public ApiResponse<Page<ExchangeDTO>> getExchangeHistory(Pageable pageable) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ApiResponse.error("User not found");
            }

            List<Exchange> history = exchangeRepository.findHistoryByUser(user);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), history.size());
            List<ExchangeDTO> pageContent = history.subList(start, end)
                    .stream()
                    .map(this::buildExchangeDTO)
                    .collect(Collectors.toList());

            Page<ExchangeDTO> page = new PageImpl<>(pageContent, pageable, history.size());
            return ApiResponse.success("Exchange history retrieved", page);

        } catch (Exception e) {
            log.error("Error retrieving exchange history: {}", e.getMessage());
            return ApiResponse.error("Failed to retrieve exchange history");
        }
    }

    /**
     * Rate and review an exchange
     */
    public ApiResponse<ExchangeDTO> rateExchange(Long exchangeId, RateExchangeRequest request) {
        try {
            Exchange exchange = exchangeRepository.findById(exchangeId).orElse(null);

            if (exchange == null) {
                return ApiResponse.error("Exchange not found");
            }

            if (exchange.getStatus() != ExchangeStatus.COMPLETED) {
                return ApiResponse.error("Can only rate completed exchanges");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Users requester = userRepository.findByEmail(email).orElse(null);

            if (requester == null || !requester.getId().equals(exchange.getRequester().getId())) {
                return ApiResponse.error("You are not authorized to rate this exchange");
            }

            exchange.setRating(request.getRating());
            exchange.setFeedback(request.getFeedback());

            Exchange updatedExchange = exchangeRepository.save(exchange);

            // Create/Update review
            Review review = Review.builder()
                    .reviewer(requester)
                    .reviewee(exchange.getProvider())
                    .exchange(exchange)
                    .rating(request.getRating())
                    .comment(request.getFeedback())
                    .build();

            reviewRepository.save(review);

            return ApiResponse.success("Exchange rated successfully", buildExchangeDTO(updatedExchange));

        } catch (Exception e) {
            log.error("Error rating exchange: {}", e.getMessage());
            return ApiResponse.error("Failed to rate exchange");
        }
    }

    /**
     * Build ExchangeDTO from Exchange entity
     */
    private ExchangeDTO buildExchangeDTO(Exchange exchange) {
        return ExchangeDTO.builder()
                .id(exchange.getId())
                .requester(mapToUserResponse(exchange.getRequester()))
                .provider(mapToUserResponse(exchange.getProvider()))
                .skillOffered(mapToSkillDTO(exchange.getSkillOffered()))
                .skillRequested(mapToSkillDTO(exchange.getSkillRequested()))
                .status(exchange.getStatus())
                .sessionDate(exchange.getSessionDate())
                .sessionLink(exchange.getSessionLink())
                .rating(exchange.getRating())
                .feedback(exchange.getFeedback())
                .createdAt(exchange.getCreatedAt())
                .completedAt(exchange.getCompletedAt())
                .build();
    }

    private UserResponse mapToUserResponse(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .rollNumber(user.getRollNumber())
                .email(user.getEmail())
                .department(user.getDepartment())
                .semester(user.getSemester())
                .bio(user.getBio())
                .profilePicUrl(user.getProfilePicUrl())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private SkillDTO mapToSkillDTO(Skill skill) {
        return SkillDTO.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .popularityScore(skill.getPopularityScore())
                .build();
    }
}
