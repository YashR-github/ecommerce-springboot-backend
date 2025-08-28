package com.example.ecommerce_springboot.auth.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long jwtExpirationInMillis;


    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMs}") long jwtExpirationInMillis ) {
        this.secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtExpirationInMillis=jwtExpirationInMillis;
    }


    public String generateToken(String username, String role){
        Date now = new Date();
        Date expiryDate= new Date(now.getTime() + jwtExpirationInMillis);

        return Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch(JwtException | IllegalArgumentException ex){
            return false;
    }
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractUserRole(String token){
        return (String) Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }


}
