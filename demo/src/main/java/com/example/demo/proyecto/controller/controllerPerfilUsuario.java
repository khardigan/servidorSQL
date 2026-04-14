package com.example.demo.proyecto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyecto.dto.CrearPerfilRequestDTO;
import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Usuario;
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

    @GetMapping
    public ResponseEntity<List<PerfilUsuarioDTO>> listar() {
        return ResponseEntity.ok(service.listarPerfilesDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        PerfilUsuarioDTO perfil = service.obtenerPerfilDTO(id);
        if (perfil == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        }

        Long idToken = serviceJWT.obtenerId(token);
        String rol = serviceJWT.obtenerRol(token);

        if (!rol.equals("ADMIN") && !idToken.equals(perfil.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tienes permisos para ver este perfil");
        }

        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody CrearPerfilRequestDTO perfilDTO,
            @RequestHeader("Authorization") String authHeader) {

        // 1️⃣ Limpiar y validar token
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }

        // 2️⃣ Obtener datos del token
        Long idToken = serviceJWT.obtenerId(token);
        String rol = serviceJWT.obtenerRol(token);

        // 3️⃣ Verificar permisos: ADMIN o el propio usuario
        if (!rol.equals("ADMIN") && !idToken.equals(perfilDTO.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No puedes crear un perfil para otro usuario");
        }

        // 4️⃣ Crear perfil usando la instancia correcta del service
        try {
            PerfilUsuarioDTO creado = service.guardarPerfil(perfilDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear perfil: " + e.getMessage());
        }
    }

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

        if (!rol.equals("ADMIN") && !idToken.equals(perfilExistente.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para actualizar este perfil");
        }

        PerfilUsuarioDTO actualizado = service.actualizarPerfil(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader) {

        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Long idToken = serviceJWT.obtenerId(token);
        String rol = serviceJWT.obtenerRol(token);

        PerfilUsuarioDTO perfilExistente = service.obtenerPerfilDTO(id);
        if (perfilExistente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        }

        if (!rol.equals("ADMIN") && !idToken.equals(perfilExistente.getUsuarioId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para eliminar este perfil");
        }

        boolean ok = service.eliminarPerfil(id);
        if (!ok)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuario")
    public ResponseEntity<?> obtenerUsuarioDelPerfil(@PathVariable Integer id) {
        PerfilUsario p = service.buscarPerfilPorId(id);
        if (p == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        Usuario u = service.obtenerUsuarioDelPerfil(id);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(u);
    }
}
