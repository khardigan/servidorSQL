package com.example.demo.proyecto.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyecto.dto.CrearPerfilRequestDTO;
import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.servicePerfilUsuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/perfiles")
public class controllerPerfilUsuario {

    private final servicePerfilUsuario service;
    private final serviceJWT serviceJWT;

    public controllerPerfilUsuario(servicePerfilUsuario service, serviceJWT serviceJWT) {
        this.service = service;
        this.serviceJWT = serviceJWT;
    }

    // Te da el perfil del usuario. (Tiene que recibir el ID y el Token)
    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<?> obtener(@PathVariable Integer
     * id, @RequestHeader("Authorization") String authHeader) {
     * String token = serviceJWT.limpiarToken(authHeader);
     * if (token == null || !serviceJWT.esTokenValido(token)) {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
     * body("Token inválido o ausente");
     * }
     * 
     * PerfilUsuarioDTO perfil = service.obtenerPerfilDTO(id);
     * if (perfil == null) {
     * return
     * ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
     * }
     * 
     * Long idToken = serviceJWT.obtenerId(token);
     * String rol = serviceJWT.obtenerRol(token);
     * 
     * if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(perfil.getUsuarioId()))
     * {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
     * body("No tienes permisos para ver este perfil");
     * }
     * 
     * return ResponseEntity.ok(perfil);
     * }
     */

    // Te actualiza el perfil. (Tiene que recibir el ID y el Token)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CrearPerfilRequestDTO datos,
            @RequestHeader("Authorization") String authHeader) {

        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }

        Long idToken = serviceJWT.obtenerId(token);
        String rol = serviceJWT.obtenerRol(token);

        PerfilUsuarioDTO perfilExistente = service.obtenerPerfilDTO(id);
        if (perfilExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        }

        if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(perfilExistente.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para actualizar este perfil");
        }

        try {
            PerfilUsuarioDTO actualizado = service.actualizarPerfil(id, datos);
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
            }
            return ResponseEntity.ok(actualizado);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El nombre de perfil ya está en uso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }

}
