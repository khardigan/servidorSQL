package com.example.demo.proyecto.controller;

import java.util.List;
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
import com.example.demo.proyecto.dto.CrearProductoDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceProducto;

import jakarta.validation.Valid;

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

    //---------------- Listar --------------
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

    // ---------------- Crear temporal ----------------
    @PostMapping("/pending")
    public ResponseEntity<?> crearProductoTemporal(
            @Valid @RequestBody CrearProductoDTO dtoRequest,
            @RequestHeader("Authorization") String authHeader) {

        System.out.println("authHeader recibido: " + authHeader);
        String token = extraerToken(authHeader);
        System.out.println("Token extraído: " + token);

        boolean valido = jwtService.esTokenValido(token);
        System.out.println("¿Token válido? " + valido);

        String subject = jwtService.obtenerSubject(token);
        System.out.println("Subject: " + subject);

        Usuario usuario = encontrarUsuarioPorNombre(subject);
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO temp = service.guardarProductoTemporal(dtoRequest, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(temp);
    }


    // ---------------- Confirmar producto ----------------
    @PostMapping("/confirm/{tempId}")
    public ResponseEntity<?> confirmarProducto(
            @PathVariable Long tempId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Usuario admin = encontrarUsuarioPorNombre(jwtService.obtenerSubject(token));
        if (admin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        try {
            ProductoDTO confirmado = service.confirmarProducto(tempId, admin);
            return ResponseEntity.ok(confirmado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ---------------- Rechazar producto ----------------
    @DeleteMapping("/reject/{tempId}")
    public ResponseEntity<?> rechazarProducto(
            @PathVariable Long tempId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Usuario admin = encontrarUsuarioPorNombre(jwtService.obtenerSubject(token));
        if (admin == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        try {
            boolean ok = service.rechazarProducto(tempId, admin);
            return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ---------------- Actualizar ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody CrearProductoDTO dtoRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Usuario usuario = encontrarUsuarioPorNombre(jwtService.obtenerSubject(token));
        if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO actualizado = service.actualizarProducto(id, dtoRequest, usuario);
        if (actualizado == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        return ResponseEntity.ok(actualizado);
    }

    // ---------------- Eliminar ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String rol = jwtService.obtenerRol(token);
        ProductoDTO producto = service.obtenerProductoDTO(id);
        if (producto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        if (!rol.equals("ADMIN")) {
            String nombreUsuario = jwtService.obtenerSubject(token);
            if (producto.getUsuarioRegistradorId() == null ||
                !producto.getUsuarioRegistradorId().equals(encontrarUsuarioPorNombre(nombreUsuario).getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo el dueño o admin pueden eliminar este producto");
            }
        }

        boolean ok = service.eliminarProducto(producto.getId());
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        return ResponseEntity.noContent().build();
    }

    //--------------- Listas del producto ----------
    @GetMapping("/{id}/listas")
    public ResponseEntity<?> obtenerListasDelProducto(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p.getListas());
    }
    private String extraerToken(String authHeader) {
        if (authHeader == null) return null;
        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        // Si no empieza con Bearer, devolvemos null
        return null;
    }


    private Usuario encontrarUsuarioPorNombre(String nombre) {
        if (nombre == null) return null;
        Optional<Usuario> opt = repoUsuario.findAll().stream()
                .filter(u -> nombre.equals(u.getNombre()))
                .findFirst();
        return opt.orElse(null);
    }
}
