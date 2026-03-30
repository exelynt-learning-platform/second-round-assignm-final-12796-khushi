package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration:86400000}") // default 1 day
    private long EXPIRATION_TIME;

    // ✅ GENERATE TOKEN WITH ROLE
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // ✅ add role in token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // ✅ EXTRACT USERNAME
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ EXTRACT ROLE
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);

            // check expiration
            return !claims.getExpiration().before(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    // ✅ COMMON METHOD
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}