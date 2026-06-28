package com.camaradacamarao.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration.
 *
 * Key decisions:
 *  - Stateless session (STATELESS) — no server-side state, every request is authenticated via JWT.
 *  - httpBasic REMOVED — credentials no longer travel with every request.
 *  - JwtFilter runs BEFORE UsernamePasswordAuthenticationFilter so it can populate
 *    the SecurityContext before Spring Security tries to do its own authentication.
 *  - Public routes are explicitly listed; all others require authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz

                // ── Public routes ──────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/menu", "/api/menu/*").permitAll()

                // ── CUSTOMER ───────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET,  "/api/orders/mine").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/feedbacks").hasRole("CUSTOMER")

                // ── ATTENDANT or ADMINISTRATOR ─────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/orders").hasAnyRole("ATTENDANT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("ATTENDANT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/ingredients").hasAnyRole("ATTENDANT", "ADMINISTRATOR")

                // ── ADMINISTRATOR only ─────────────────────────────────────
                .requestMatchers("/api/stock/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/api/reports/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/menu/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.POST,   "/api/menu").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT,    "/api/menu/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/api/users/attendants/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/feedbacks").hasRole("ADMINISTRATOR")

                // ── Anything else requires authentication ──────────────────
                .anyRequest().authenticated()
            )
            // JWT filter intercepts before Spring's own auth filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the AuthenticationManager so AuthController can call
     * authenticationManager.authenticate(...) to validate credentials.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
