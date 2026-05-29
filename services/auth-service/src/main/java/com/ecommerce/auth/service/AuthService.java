package com.ecommerce.auth.service;

import com.ecommerce.auth.api.dto.AuthDtos.LoginRequest;
import com.ecommerce.auth.api.dto.AuthDtos.RegisterRequest;
import com.ecommerce.auth.api.dto.AuthDtos.TokenResponse;
import com.ecommerce.auth.api.dto.AuthDtos.UserResponse;
import com.ecommerce.auth.domain.RefreshToken;
import com.ecommerce.auth.domain.Role;
import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.repository.RefreshTokenRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.common.security.JwtProperties;
import com.ecommerce.common.security.JwtService;
import com.ecommerce.common.web.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_AUTH");

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User(request.email(), passwordEncoder.encode(request.password()), Role.CUSTOMER);
        userRepository.save(user);
        auditLog.info("user_registered userId={} email={}", user.getId(), user.getEmail());
        return toUserResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            auditLog.warn("login_failed email={}", request.email());
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (refreshToken.isExpired()) {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User not found"));
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        return issueTokens(user);
    }

    public UserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        return toUserResponse(user);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getId().toString(),
                Map.of("email", user.getEmail(), "role", user.getRole().name()));
        String refreshTokenValue = jwtService.generateRefreshToken(user.getId().toString());
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs());
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshTokenValue, expiresAt));
        auditLog.info("tokens_issued userId={}", user.getId());
        return new TokenResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                jwtProperties.accessTokenExpirationMs() / 1000);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId().toString(), user.getEmail(), user.getRole().name());
    }
}
