package com.teknisio.security;

import com.teknisio.model.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms}")
  private long accessTokenExpirationMs;

  public String generateAccessToken(User user) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + accessTokenExpirationMs);

    return Jwts.builder()
      .subject(user.getIdUser().toString())
      .claim("email", user.getEmail())
      .claim("role", user.getRole().name())
      .issuedAt(now)
      .expiration(expiration)
      .signWith(getSigningKey())
      .compact();
  }

  public long getAccessTokenExpirationMs() {
    return accessTokenExpirationMs;
  }

  public UUID extractUserId(String token) {
    return UUID.fromString(extractAllClaims(token).getSubject());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String subject = extractAllClaims(token).getSubject();
    return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
      throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
    }

    return Keys.hmacShaKeyFor(keyBytes);
  }
}
