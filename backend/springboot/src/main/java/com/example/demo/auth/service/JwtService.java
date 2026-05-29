package com.example.demo.auth.service;

import com.example.demo.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecurityProperties securityProperties;

  public JwtService(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  public String generateToken(String subject) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(securityProperties.expirationMinutes(), ChronoUnit.MINUTES);

    return Jwts.builder()
        .subject(subject)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(signingKey())
        .compact();
  }

  public String extractSubject(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (Exception ex) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey signingKey() {
    byte[] secret = securityProperties.secret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(secret);
  }
}
