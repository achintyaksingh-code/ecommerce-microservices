package com.ecommerce.auth.service;

import com.ecommerce.auth.api.dto.AuthDtos.LoginRequest;
import com.ecommerce.auth.api.dto.AuthDtos.RegisterRequest;
import com.ecommerce.auth.api.dto.AuthDtos.TokenResponse;
import com.ecommerce.auth.repository.RefreshTokenRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.common.security.JwtProperties;
import com.ecommerce.common.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void registerLoginAndRefresh() {
        authService.register(new RegisterRequest("user@example.com", "password123"));
        TokenResponse login = authService.login(new LoginRequest("user@example.com", "password123"));
        assertThat(login.accessToken()).isNotBlank();
        assertThat(login.refreshToken()).isNotBlank();

        TokenResponse refreshed = authService.refresh(login.refreshToken());
        assertThat(refreshed.accessToken()).isNotBlank();
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(refreshTokenRepository.count()).isGreaterThanOrEqualTo(1);
    }
}
