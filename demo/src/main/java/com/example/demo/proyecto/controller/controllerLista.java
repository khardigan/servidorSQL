package com.example.demo.proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyecto.dto.CrearListaRequestDTO;
import com.example.demo.proyecto.dto.ListaDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceLista;
import com.example.demo.proyecto.service.serviceProducto;

import jakarta.validation.Valid;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/listas")
public class controllerLista {

    private final serviceLista service;
    private final serviceProducto serviceProducto;
    private final repositoryUsuario repoUsuario;
    private final serviceJWT jwtService;

    public controllerLista(serviceLista service, serviceProducto serviceProducto,
                           repositoryUsuario repoUsuario, serviceJWT jwtService) {
        this.service = service;
        this.serviceProducto = serviceProducto;
        this.repoUsuario = repoUsuario;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<ListaDTO>> listar() {
        return ResponseEntity.ok(service.listarListasDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLista(@PathVariable Long id) {
        ListaDTO lista = service.obtenerListaDTO(id);
        if (lista == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crearLista(@RequestBody @Valid CrearListaRequestDTO request, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario usuarioDueno = encontrarUsuarioPorNombre(nombreUsuario);
        if (usuarioDueno == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ListaDTO creado = service.guardarLista(request, usuarioDueno); 
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLista(@PathVariable Long id, @RequestBody CrearListaRequestDTO request, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");

        String nombreUsuario = jwtService.obtenerSubject(token);
        String rol = jwtService.obtenerRol(token);
        ListaDTO existente = service.obtenerListaDTO(id);
        if (existente == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");

        if (!rol.equals("ADMIN")) {
            if (!existente.getUsuarioDuenoId().equals(encontrarUsuarioPorNombre(nombreUsuario).getId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo el dueño o admin pueden actualizar esta lista");
        }

        ListaDTO actualizado = service.actualizarLista(id, request); 
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLista(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");

        String nombreUsuario = jwtService.obtenerSubject(token);
        String rol = jwtService.obtenerRol(token);
        ListaDTO lista = service.obtenerListaDTO(id);
        if (lista == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");

        if (!rol.equals("ADMIN")) {
            if (!lista.getUsuarioDuenoId().equals(encontrarUsuarioPorNombre(nombreUsuario).getId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo el dueño o admin pueden eliminar esta lista");
        }
        boolean ok = service.eliminarLista(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<?> obtenerUsuariosDeLista(@PathVariable Long id) {
        ListaDTO l = service.obtenerListaDTO(id);
        if (l == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(service.obtenerUsuariosDeLista(id));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDeLista(@PathVariable Long id) {
        ListaDTO l = service.obtenerListaDTO(id);
        if (l == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(service.obtenerProductosDeLista(id));
    }

    private Usuario encontrarUsuarioPorNombre(String nombre) {
        if (nombre == null) return null;
        Optional<Usuario> opt = repoUsuario.findAll().stream()
                .filter(u -> nombre.equals(u.getNombre()))
                .findFirst();
        return opt.orElse(null);
    }
}
