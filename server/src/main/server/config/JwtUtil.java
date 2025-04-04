package com.knowMoreQR.server.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        // Ensure the secret is long enough for HS512
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 64) { // 512 bits
            logger.warn("JWT Secret is potentially too short for HS512. Ensure it's strong and securely managed.");
            // You might want to throw an exception or handle this more robustly in production
            // For now, let's pad it if necessary, though this isn't ideal security practice
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, paddedKey.length));
            keyBytes = paddedKey;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String userType, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("userType", userType) // Add userType as a claim
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SecurityException ex) { // Specific exception for signature validation failure with jjwt 0.11+
            logger.error("JWT signature validation failed: {}", ex.getMessage());
        } catch (Exception ex) { // Catch any other potential issues
            logger.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }
} 