package com.landregistry.controller;

import com.landregistry.dto.ApiResponse;
import com.landregistry.dto.AuthDTO;
import com.landregistry.entity.User;
import com.landregistry.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.LoginResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        AuthDTO.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * POST /api/auth/register
     * Registers a new user (citizen or officer).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Integer>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", user.getUserId()));
    }
}
