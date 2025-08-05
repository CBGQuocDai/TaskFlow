package com.backend.ToDoList.utils;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtUtils {

    private final static int EXPIRATION_TIME = 10000000;
    private final SecretKey secretKey;
    public  JwtUtils(@Value("${jwt.secret}") String secret ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_TIME))
                .signWith(this.secretKey)
                .compact();
    }
    public String getEmailFormToken(String token) {
        return Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public Claims getContent(String token) {
        return Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
