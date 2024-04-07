package com.sidha.api.utils;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.sidha.api.security.jwt.JwtUtils;

public class AuthUtils {

    @Autowired
    private static JwtUtils jwtUtils;
    
    public static boolean isKlien(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals("klien");
        }
        return false;
    }

    public static UUID getUserId(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return UUID.fromString(jwtUtils.getClaimsFromJwtToken(token).getSubject());
        }
        return null;
    }
}
