package com.landregistry.service;

import com.landregistry.dto.AuthDTO;
import com.landregistry.entity.User;
import com.landregistry.repository.UserRepository;
import com.landregistry.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Set<String> SELF_REGISTERABLE_ROLES = Set.of("ROLE_CITIZEN");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("userId", user.getUserId());

        String token = jwtUtil.generateToken(request.getUsername(), claims);

        return new AuthDTO.LoginResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRoles(),
                jwtUtil.getExpirationTime()
        );
    }

    public User register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered: " + request.getEmail());
        }

        String requestedRole = request.getRole() != null ? request.getRole().trim().toUpperCase() : "ROLE_CITIZEN";
        String role = SELF_REGISTERABLE_ROLES.contains(requestedRole) ? requestedRole : "ROLE_CITIZEN";

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .aadharNumber(request.getAadharNumber())
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(role))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("Registered new user: {} with role: {}", request.getUsername(), role);
        return user;
    }
}
