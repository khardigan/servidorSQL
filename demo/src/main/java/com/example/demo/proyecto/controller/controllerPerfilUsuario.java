package com.example.demo.proyecto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.service.servicePerfilUsuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/perfiles")
public class controllerPerfilUsuario {

    private final servicePerfilUsuario service;

    public controllerPerfilUsuario(servicePerfilUsuario service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PerfilUsuarioDTO>> listar() {
        return ResponseEntity.ok(service.listarPerfilesDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        PerfilUsuarioDTO perfil = service.obtenerPerfilDTO(id);
        if (perfil == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PerfilUsuarioDTO perfilDTO) {
        try {
            PerfilUsuarioDTO creado = service.guardarPerfil(perfilDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear perfil: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody PerfilUsuarioDTO datos) {
        PerfilUsuarioDTO actualizado = service.actualizarPerfil(id, datos);
        if (actualizado == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean ok = service.eliminarPerfil(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuario")
    public ResponseEntity<?> obtenerUsuarioDelPerfil(@PathVariable Integer id) {
        PerfilUsario p = service.buscarPerfilPorId(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        Usuario u = service.obtenerUsuarioDelPerfil(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(u);
    }
}
