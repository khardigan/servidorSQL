package com.example.demo.proyecto.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceProducto;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/productos")
public class controllerProducto {

    private final serviceProducto service;
    private final repositoryUsuario repoUsuario;
    private final serviceJWT jwtService;

    public controllerProducto(serviceProducto service, repositoryUsuario repoUsuario, serviceJWT jwtService) {
        this.service = service;
        this.repoUsuario = repoUsuario;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(service.listarProductosDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }
        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario u = encontrarUsuarioPorNombre(nombreUsuario);
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO dto = new ProductoDTO();
        try {
            dto.setNombre(payload.get("nombre") != null ? payload.get("nombre").toString() : null);
            dto.setDescripcion(payload.get("descripcion") != null ? payload.get("descripcion").toString() : null);
            dto.setPrecio(payload.get("precio") != null ? Double.valueOf(payload.get("precio").toString()) : null);
            dto.setCantidad(payload.get("cantidad") != null ? ((Number) payload.get("cantidad")).intValue() : null);
            dto.setUsuarioRegistradorId(u.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos de producto inválidos: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarProducto(dto));
    }

   @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String authHeader) {
        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario u = encontrarUsuarioPorNombre(nombreUsuario);
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO existente = service.obtenerProductoDTO(id);
        if (existente == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        try {
            if (payload.get("nombre") != null) existente.setNombre(payload.get("nombre").toString());
            if (payload.get("descripcion") != null) existente.setDescripcion(payload.get("descripcion").toString());
            if (payload.get("precio") != null) existente.setPrecio(Double.valueOf(payload.get("precio").toString()));
            if (payload.get("cantidad") != null) existente.setCantidad(((Number) payload.get("cantidad")).intValue());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos de producto inválidos: " + e.getMessage());
        }

        existente.setUsuarioRegistradorId(u.getId());
        ProductoDTO actualizado = service.actualizarProducto(id, existente);
        if (actualizado == null) 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(actualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String rol = jwtService.obtenerRol(token);
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        if (!rol.equals("ADMIN")) {
            String nombreUsuario = jwtService.obtenerSubject(token);
            if (p.getUsuarioRegistradorId() == null || !p.getUsuarioRegistradorId().equals(encontrarUsuarioPorNombre(nombreUsuario).getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo el dueño o admin pueden eliminar este producto");
            }
        }

        boolean ok = service.eliminarProducto(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/listas")
    public ResponseEntity<?> obtenerListasDelProducto(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p.getListas());
    }

    private String extraerToken(String authHeader) {
        if (authHeader == null) return null;
        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) return authHeader.substring(7).trim();
        return authHeader;
    }

    private Usuario encontrarUsuarioPorNombre(String nombre) {
        if (nombre == null) return null;
        Optional<Usuario> opt = repoUsuario.findAll().stream().filter(u -> nombre.equals(u.getNombre())).findFirst();
        return opt.orElse(null);
    }
}
