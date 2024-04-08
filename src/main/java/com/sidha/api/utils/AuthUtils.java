package com.sidha.api.utils;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sidha.api.model.enumerator.Role;
import com.sidha.api.security.jwt.JwtUtils;

@Component
public class AuthUtils {

    @Autowired
    private JwtUtils jwtUtils;
    
    public UUID getUserId(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return UUID.fromString(jwtUtils.getClaimsFromJwtToken(token).getSubject());
        }
        return null;
    }

    public boolean isMatch(String token, UUID userId) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).getSubject().equals(userId.toString());
        }
        return false;
    }
    
    public boolean isKlien(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals(Role.KLIEN.toString());
        }
        return false;
    }


    public boolean isKaryawan(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals(Role.KARYAWAN.toString());
        }
        return false;
    }

    public boolean isAdmin(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals(Role.ADMIN.toString());
        }
        return false;
    }

    public boolean isSopir(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return jwtUtils.getClaimsFromJwtToken(token).get("role").equals(Role.SOPIR.toString());
        }
        return false;
    }
}
