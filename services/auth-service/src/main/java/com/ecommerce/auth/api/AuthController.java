package com.ecommerce.auth.api;

import com.ecommerce.auth.api.dto.AuthDtos.LoginRequest;
import com.ecommerce.auth.api.dto.AuthDtos.RefreshRequest;
import com.ecommerce.auth.api.dto.AuthDtos.RegisterRequest;
import com.ecommerce.auth.api.dto.AuthDtos.TokenResponse;
import com.ecommerce.auth.api.dto.AuthDtos.UserResponse;
import com.ecommerce.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return authService.getUser(userId);
    }
}
