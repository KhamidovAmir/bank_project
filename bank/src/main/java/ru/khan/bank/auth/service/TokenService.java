package ru.khan.bank.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import ru.khan.bank.auth.config.JwtProperties;
import ru.khan.bank.auth.dto.JwtUser;
import ru.khan.bank.user.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public TokenService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );

    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + properties.expiration());

        return Jwts.builder()
                .subject(user.getPublicId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    public JwtUser parse(String token){
        return new JwtUser(
                extractPublicId(token),
                extractEmail(token),
                extractRole(token)
        );
    }

    private UUID extractPublicId(String token){
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
        return UUID.fromString(subject);
    }
    private String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }
    private String extractRole(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

}
