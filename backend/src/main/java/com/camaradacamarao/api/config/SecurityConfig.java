package com.camaradacamarao.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public routes
                .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu", "/api/menu/*").permitAll()
                
                // CUSTOMER
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/orders/mine").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/feedbacks").hasRole("CUSTOMER")
                
                // ATTENDANT or ADMINISTRATOR
                .requestMatchers(HttpMethod.GET, "/api/orders").hasAnyRole("ATTENDANT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("ATTENDANT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/ingredients").hasAnyRole("ATTENDANT", "ADMINISTRATOR")
                
                // ADMINISTRATOR
                .requestMatchers("/api/stock/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/api/reports/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/menu/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.POST, "/api/menu").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/menu/**").hasRole("ADMINISTRATOR")
                .requestMatchers("/api/users/attendants/**").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/feedbacks").hasRole("ADMINISTRATOR")
                
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
