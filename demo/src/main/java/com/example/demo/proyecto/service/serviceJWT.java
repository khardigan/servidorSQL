package com.example.demo.proyecto.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class serviceJWT {

    private static final String SECRET = "esta_es_una_clave_super_secreta_de_ejemplo_1234567890";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String sub, String rol, Long id) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(sub) // sub = nombre de usuario
                .claim("rol", rol) // rol = rol del usuario
                .claim("id", id) // <--- el ID del usuario
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String obtenerSubject(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public String obtenerRol(String token) {
        return parseClaims(token).getBody().get("rol", String.class);
    }

    public Long obtenerId(String token) {
        Object idClaim = parseClaims(token).getBody().get("id");
        if (idClaim == null)
            return null;
        if (idClaim instanceof Number num) {
            return num.longValue();
        }
        return Long.valueOf(idClaim.toString());
    }

    public boolean esTokenValido(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            System.err.println("Error validando JWT en esTokenValido: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String limpiarToken(String authHeader) {
        if (authHeader == null)
            return null;
        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer "))
            return authHeader.substring(7).trim();
        return authHeader;
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
}
