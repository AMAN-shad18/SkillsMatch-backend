package com.skillsBridge.skillBridge.security;

import com.skillsBridge.skillBridge.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class jwtService {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate access token for a user
     */
    public String generateAccessToken(Users user) {
        return generateAccessToken(user.getEmail(), new HashMap<>());
    }

    /**
     * Generate access token with custom claims
     */
    public String generateAccessToken(String email, Map<String, Object> claims) {
        Map<String, Object> claimsWithType = new HashMap<>(claims);
        claimsWithType.put("type", "access");
        claimsWithType.put("email", email);
        return createToken(claimsWithType, email, accessTokenExpiration);
    }

    /**
     * Generate refresh token for a user
     */
    public String generateRefreshToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("email", user.getEmail());
        return createToken(claims, user.getEmail(), refreshTokenExpiration);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error creating JWT token: {}", e.getMessage());
            throw new RuntimeException("Error generating token");
        }
    }

    /**
     * Extract username/email from token
     */
    public String extractUsername(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate token
     */
    public Boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired");
            return false;
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = jwtUtil.extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Get access token expiration time in seconds
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
