package com.skillsBridge.skillBridge.service;

import com.skillsBridge.skillBridge.dto.request.*;
import com.skillsBridge.skillBridge.dto.response.ApiResponse;
import com.skillsBridge.skillBridge.dto.response.AuthResponse;
import com.skillsBridge.skillBridge.dto.response.UserResponse;
import com.skillsBridge.skillBridge.entity.*;
import com.skillsBridge.skillBridge.enums.UserRole;
import com.skillsBridge.skillBridge.repository.*;
import com.skillsBridge.skillBridge.security.jwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private jwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private static final int TOKEN_EXPIRY_HOURS = 24;

    /**
     * Register a new user
     */
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        try {
            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Email already registered");
            }

            if (userRepository.existsByRollNumber(request.getRollNumber())) {
                return ApiResponse.error("Roll number already exists");
            }

            // Create new user
            Users user = Users.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .rollNumber(request.getRollNumber())
                    .department(request.getDepartment())
                    .semester(request.getSemester())
                    .bio(request.getBio())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.STUDENT)
                    .isVerified(true)
                    .build();

            Users savedUser = userRepository.save(user);

            UserResponse userResponse = mapToUserResponse(savedUser);

            AuthResponse authResponse = AuthResponse.builder()
                    .user(userResponse)
                    .build();

            return ApiResponse.success("Registration successful", authResponse);

        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return ApiResponse.error("Registration failed");
        }
    }

    /**
     * Login user
     */
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            // Find user by email
            Users user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                return ApiResponse.error("Invalid email or password");
            }

            // Check password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                return ApiResponse.error("Invalid email or password");
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Save refresh token to database
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                    .revoked(false)
                    .build();

            refreshTokenRepository.save(refreshTokenEntity);

            UserResponse userResponse = mapToUserResponse(user);

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .user(userResponse)
                    .build();

            return ApiResponse.success("Login successful", authResponse);

        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ApiResponse.error("Login failed");
        }
    }

    /**
     * Refresh access token
     */
    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenRepository
                    .findByToken(request.getRefreshToken())
                    .orElse(null);

            if (refreshToken == null) {
                return ApiResponse.error("Invalid refresh token");
            }

            if (refreshToken.isRevoked()) {
                return ApiResponse.error("Refresh token has been revoked");
            }

            if (LocalDateTime.now().isAfter(refreshToken.getExpiryDate())) {
                return ApiResponse.error("Refresh token has expired");
            }

            if (!jwtService.validateToken(request.getRefreshToken())) {
                return ApiResponse.error("Invalid refresh token");
            }

            Users user = refreshToken.getUser();

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(user);

            UserResponse userResponse = mapToUserResponse(user);

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(request.getRefreshToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .user(userResponse)
                    .build();

            return ApiResponse.success("Token refreshed successfully", authResponse);

        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ApiResponse.error("Token refresh failed");
        }
    }

    /**
     * Send forgot password email
     */
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        try {
            Users user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (user == null) {
                // For security, don't reveal if email exists
                return ApiResponse.success("If the email exists, a password reset link will be sent");
            }

            // Delete existing reset tokens for this user
            passwordResetTokenRepository.deleteByUser(user);

            // Generate reset token
            String resetToken = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .user(user)
                    .token(resetToken)
                    .expiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                    .build();

            passwordResetTokenRepository.save(passwordResetToken);

            // Send reset email
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

            return ApiResponse.success("If the email exists, a password reset link will be sent");

        } catch (Exception e) {
            log.error("Error during forgot password: {}", e.getMessage());
            return ApiResponse.error("Forgot password request failed");
        }
    }

    /**
     * Reset password with token
     */
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository
                    .findByToken(request.getToken())
                    .orElse(null);

            if (resetToken == null) {
                return ApiResponse.error("Invalid reset token");
            }

            if (resetToken.isExpired()) {
                return ApiResponse.error("Reset token has expired");
            }

            Users user = resetToken.getUser();
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Delete the reset token
            passwordResetTokenRepository.delete(resetToken);

            // Send confirmation email
            emailService.sendPasswordChangeConfirmationEmail(user.getEmail());

            return ApiResponse.success("Password reset successfully");

        } catch (Exception e) {
            log.error("Error during password reset: {}", e.getMessage());
            return ApiResponse.error("Password reset failed");
        }
    }

    /**
     * Map Users entity to UserResponse DTO
     */
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
}
