package com.camaradacamarao.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Centralized JWT utility: signs, validates, and parses tokens.
 *
 * Required environment variables:
 *   JWT_SECRET         — at least 32-character secret key
 *   JWT_EXPIRATION_MS  — token lifetime in milliseconds (default: 28800000 = 8 hours)
 */
@Component
public class JwtConfig {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtConfig(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:28800000}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT containing the user's email (subject) and role (claim).
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates the token signature and expiration.
     * Returns true only if the token is intact and not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Extracts the email (subject) from a valid token. */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extracts the role claim from a valid token. */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
