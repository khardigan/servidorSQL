package com.example.demo.proyecto.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final serviceJWT jwtService;

    public JwtAuthenticationFilter(serviceJWT jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. SI ES UNA PETICIÓN OPTIONS, DÉJALA PASAR SIN MIRAR TOKEN
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            final String userEmail = jwtService.obtenerSubject(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.esTokenValido(jwt)) {
                    String rol = jwtService.obtenerRol(jwt);
                    java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
                    if (rol != null) {
                        authorities.add(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + rol));
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(rol));
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expirado, no hace falta ensuciar el log con el stacktrace completo
            // solo si quieres ver un aviso corto:
            // System.err.println("Token JWT expirado");
        } catch (Exception e) {
            // Otros errores (token malformado, firma inválida, etc.)
            System.err.println("Error procesando JWT: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
