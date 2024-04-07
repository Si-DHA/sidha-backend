package com.sidha.api.utils;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sidha.api.security.jwt.JwtUtils;

@Component
public class AuthUtils {

    @Autowired
    private JwtUtils jwtUtils;
    
    public boolean isKlien(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals("klien");
        }
        return false;
    }

    public UUID getUserId(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return UUID.fromString(jwtUtils.getClaimsFromJwtToken(token).getSubject());
        }
        return null;
    }
}
