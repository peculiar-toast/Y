package com.alececco.y.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

//@Configuration
public class JwtTokenUtil {
    private final SecretKey secret = Keys.hmacShaKeyFor("my_secret_key".getBytes());
    private final long expiration = 24 * 60 * 60;

    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getEncoded()))
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseEncryptedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseEncryptedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public boolean validateToken(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername())
                && !isExpired(token);
    }
}
