package com.example.ecommerce_springboot.auth.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;


@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long jwtExpirationInMillis;


    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMs}") long jwtExpirationInMillis ) {
        this.secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.jwtExpirationInMillis=jwtExpirationInMillis;
    }


    public String generateToken(Long userId, String role){
        Date now = new Date();
        Date expiryDate= new Date(now.getTime() + jwtExpirationInMillis);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer("CommerceHub_BY_Yash")
                .claim("role",role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie generateTokenCookie(String token){
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(false)//make true for production
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpirationInMillis))
                .build();
        return cookie;
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

    public String extractSubject(String token){
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
