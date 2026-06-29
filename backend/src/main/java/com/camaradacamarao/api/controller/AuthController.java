package com.camaradacamarao.api.controller;

import com.camaradacamarao.api.config.JwtConfig;
import com.camaradacamarao.api.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles authentication. This is the ONLY entry point for obtaining a JWT.
 *
 * POST /api/auth/login
 *   Body: { "email": "...", "password": "..." }
 *   Response 200: { "token": "<jwt>", "role": "CUSTOMER|ATTENDANT|ADMINISTRATOR" }
 *   Response 401: { "status": 401, "error": "Credenciais inválidas." }
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)          // e.g. "ROLE_CUSTOMER"
                    .findFirst()
                    .map(r -> r.replace("ROLE_", ""))            // → "CUSTOMER"
                    .orElse("UNKNOWN");

            String token = jwtConfig.generateToken(email, role);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role",  role,
                    "email", email
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", 401, "error", "Credenciais inválidas."));
        }
    }
}
