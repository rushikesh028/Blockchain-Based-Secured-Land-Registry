package com.landregistry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        private String email;

        private String aadharNumber;
        private String phoneNumber;
        private String role;  // default: ROLE_CITIZEN
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Integer userId;
        private String username;
        private String fullName;
        private java.util.Set<String> roles;
        private long expiresAt;

        public LoginResponse(String token, Integer userId, String username, String fullName,
                             Set<String> roles, long expiresAt) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.roles = roles;
            this.expiresAt = expiresAt;
        }
    }
}
