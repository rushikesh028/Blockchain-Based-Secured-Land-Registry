package com.landregistry;

import com.landregistry.entity.User;
import com.landregistry.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class LandRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LandRegistryApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "app.seed-default-users", havingValue = "true")
    public CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {

            seedUser(userRepository, encoder,
                    "admin",
                    "Admin@1234",
                    "System Administrator",
                    "admin@landregistry.gov",
                    Set.of("ROLE_ADMIN")
            );

            seedUser(userRepository, encoder,
                    "registrar",
                    "Registrar@1234",
                    "District Registrar",
                    "registrar@landregistry.gov",
                    Set.of("ROLE_REGISTRAR")
            );

            seedUser(userRepository, encoder,
                    "officer",
                    "Officer@1234",
                    "Revenue Officer",
                    "officer@landregistry.gov",
                    Set.of("ROLE_OFFICER")
            );
        };
    }

    @Bean
    @ConditionalOnProperty(name = {"app.bootstrap.admin.username", "app.bootstrap.admin.password"})
    public CommandLineRunner bootstrapAdmin(
            UserRepository userRepository,
            PasswordEncoder encoder,
            @org.springframework.beans.factory.annotation.Value("${app.bootstrap.admin.username}") String username,
            @org.springframework.beans.factory.annotation.Value("${app.bootstrap.admin.password}") String password,
            @org.springframework.beans.factory.annotation.Value("${app.bootstrap.admin.full-name:Platform Administrator}") String fullName,
            @org.springframework.beans.factory.annotation.Value("${app.bootstrap.admin.email:admin@example.com}") String email
    ) {
        return args -> seedUser(
                userRepository,
                encoder,
                username,
                password,
                fullName,
                email,
                Set.of("ROLE_ADMIN")
        );
    }

    private void seedUser(UserRepository userRepository,
                          PasswordEncoder encoder,
                          String username,
                          String rawPassword,
                          String fullName,
                          String email,
                          Set<String> roles) {

        if (!StringUtils.hasText(username) || !StringUtils.hasText(rawPassword)) {
            log.info(">>> Skipping user seed because username or password is blank");
            return;
        }

        if (!userRepository.existsByUsername(username)) {

            User user = User.builder()
                    .username(username)
                    .password(encoder.encode(rawPassword))
                    .fullName(fullName)
                    .email(email)
                    .roles(roles)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info(">>> Seeded user: {}", username);
        }
    }
}
