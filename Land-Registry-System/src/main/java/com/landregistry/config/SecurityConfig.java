package com.landregistry.config;

import com.landregistry.config.JwtAuthenticationFilter;
import com.landregistry.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.relaxed:true}")
    private boolean relaxedSecurity;

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Value("#{'${app.security.allowed-origins:http://localhost:63342,http://127.0.0.1:63342,http://localhost:8080,http://127.0.0.1:8080}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .requestMatchers(
                                    "/",
                                    "/index.html",
                                    "/error",
                                    "/favicon.ico",
                                    "/land-registry-blockchain/static/index.html",
                                    "/land-registry-blockchain_00/land-registry-blockchain/static/index.html",
                                    "/api/auth/**"
                            ).permitAll()
                            .requestMatchers("/actuator/health").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    if (h2ConsoleEnabled) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }

                    if (relaxedSecurity) {
                        auth.requestMatchers(HttpMethod.GET, "/api/land/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/land/register").permitAll()
                                .requestMatchers("/api/land/blockchain/**").permitAll();
                    } else {
                        auth.requestMatchers(HttpMethod.GET, "/api/land/blockchain/info").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/land/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/land/register").hasAnyRole("REGISTRAR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/land/transfer/**").hasAnyRole("REGISTRAR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/land/mutate/**").hasAnyRole("REGISTRAR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/land/encumber/**").hasAnyRole("REGISTRAR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/land/dispute/**").hasAnyRole("OFFICER", "REGISTRAR", "ADMIN")
                                .requestMatchers("/api/land/blockchain/**").hasRole("ADMIN");
                    }

                    auth.anyRequest().authenticated();
                })

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> {
                    if (h2ConsoleEnabled) {
                        headers.frameOptions(frame -> frame.disable());
                    }
                });

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList()));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
