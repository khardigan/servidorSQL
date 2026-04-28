package com.example.demo.proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.dto.CrearProductoDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceProducto;

import jakarta.validation.Valid;

@RestController
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

    // ---------------- Listar --------------
    // Devuelve la lista de todos los productos.
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(service.listarProductosDTO());
    }

    // Busca productos por nombre o descripción. (Tiene que recibir el Token)
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(service.buscarProductosDTO(q));
    }

    // Devuelve la lista de categorías únicas.
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    // Devuelve la lista de supermercados únicos.
    @GetMapping("/supermercados")
    public ResponseEntity<List<String>> listarSupermercados() {
        return ResponseEntity.ok(service.listarSupermercados());
    }

    // Te da el producto por ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p);
    }

    // ---------------- Crear temporal ----------------
    // Crea un producto temporal. (Tiene que recibir el Token y el Producto)
    @PostMapping("/pending")
    public ResponseEntity<?> crearProductoTemporal(
            @Valid @RequestBody CrearProductoDTO dtoRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");

        String subject = jwtService.obtenerSubject(token);
        Usuario usuario = encontrarUsuarioPorNombre(subject);
        if (usuario == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO temp = service.guardarProductoTemporal(dtoRequest, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(temp);
    }

    // ---------------- Confirmar producto ----------------
    // Confirma un producto. (Tiene que recibir el Token y el ID)
    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmarProducto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Usuario admin = encontrarUsuarioPorNombre(jwtService.obtenerSubject(token));
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        try {
            ProductoDTO confirmado = service.confirmarProducto(id, admin);
            return ResponseEntity.ok(confirmado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ---------------- Rechazar producto ----------------
    // Rechaza un producto. (Tiene que recibir el Token y el ID)
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<?> rechazarProducto(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = extraerToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        Usuario admin = encontrarUsuarioPorNombre(jwtService.obtenerSubject(token));
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        try {
            boolean ok = service.rechazarProducto(id, admin);
            return ok ? ResponseEntity.noContent().build()
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // ---------------- Actualizar ----------------
    // Actualiza un producto. (Tiene que recibir el Token y el ID)
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
        if (usuario == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ProductoDTO actualizado = service.actualizarProducto(id, dtoRequest, usuario);
        if (actualizado == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        return ResponseEntity.ok(actualizado);
    }

    // ---------------- Eliminar ----------------
    // Elimina un producto. (Tiene que recibir el Token y el ID)
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
        if (producto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        if (!"ADMIN".equalsIgnoreCase(rol)) {
            Long userId = jwtService.obtenerId(token);
            if (producto.getUsuarioRegistradorId() == null ||
                    !producto.getUsuarioRegistradorId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo el dueño o admin pueden eliminar este producto");
            }
        }

        boolean ok = service.eliminarProducto(producto.getId());
        if (!ok)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");

        return ResponseEntity.noContent().build();
    }

    // --------------- Listas del producto ----------
    // Te da las listas del producto. (Tiene que recibir el ID)
    @GetMapping("/{id}/listas")
    public ResponseEntity<?> obtenerListasDelProducto(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p.getListas());
    }

    // Te da el usuario del producto. (Tiene que recibir el ID)
    @GetMapping("/{id}/usuario")
    public ResponseEntity<?> obtenerUsuarioDelProducto(@PathVariable Long id) {
        ProductoDTO p = service.obtenerProductoDTO(id);
        if (p == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        return ResponseEntity.ok(p.getUsuarioRegistradorId());
    }

    // Extrae el token del header.
    private String extraerToken(String authHeader) {
        if (authHeader == null)
            return null;
        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        // Si no empieza con Bearer, devolvemos null
        return null;
    }

    // Busca el usuario por nombre.
    private Usuario encontrarUsuarioPorNombre(String nombre) {
        if (nombre == null)
            return null;
        Optional<Usuario> opt = repoUsuario.findAll().stream()
                .filter(u -> nombre.equals(u.getNombre()))
                .findFirst();
        return opt.orElse(null);
    }

    @GetMapping("/mediaPuntuacionComentarios")
    public ResponseEntity<?> obtenerMediaPuntuacionYComentarios() {
        List<ProductoDTO> productos = service.listarProductosDTOPuntuacion();
        return ResponseEntity.ok(productos);
    }
}
