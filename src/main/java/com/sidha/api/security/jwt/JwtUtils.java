package com.sidha.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sidha.api.model.user.UserModel;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${sidha.app.jwtSecret}")
    private String jwtSecret;

    @Value("${sidha.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserModel user) {
        var claims = Jwts.claims().setSubject(user.getId().toString());
        claims.put("name", user.getName());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("address", user.getAddress());
        claims.put("role", user.getRole().getName());

        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(new Date().getTime() + TimeUnit.MILLISECONDS.toMillis(jwtExpirationMs)));

        return Jwts.builder().setClaims(claims).signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody().get("username")
                .toString();
    }

    public String getTokenFromHeader(Map<String, String> header) {
        return header.get("authorization").substring(7);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}