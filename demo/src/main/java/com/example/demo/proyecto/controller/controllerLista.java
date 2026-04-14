package com.example.demo.proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyecto.dto.CrearListaRequestDTO;
import com.example.demo.proyecto.dto.ListaDTO;
import com.example.demo.proyecto.dto.ListaDetalleDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceLista;
import com.example.demo.proyecto.service.serviceProducto;

import jakarta.validation.Valid;

@RestController
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
        if (lista == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/mis-listas")
    public ResponseEntity<?> obtenerMisListas(@RequestHeader("Authorization") String authHeader) {
        // Endpoint que devuelve las listas asociadas al usuario autenticado, con todos
        // sus detalles integrados (dueño, integrantes y productos).
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario usuario = encontrarUsuarioPorNombre(nombreUsuario);
        if (usuario == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        List<ListaDetalleDTO> misListas = service.obtenerMisListasDetalle(usuario);
        return ResponseEntity.ok(misListas);
    }

    @PostMapping
    public ResponseEntity<?> crearLista(@RequestBody @Valid CrearListaRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario usuarioDueno = encontrarUsuarioPorNombre(nombreUsuario);
        if (usuarioDueno == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        ListaDTO creado = service.guardarLista(request, usuarioDueno);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLista(@PathVariable Long id, @RequestBody CrearListaRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtService.limpiarToken(authHeader);
            if (token == null || !jwtService.esTokenValido(token))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");

            String nombreUsuario = jwtService.obtenerSubject(token);
            String rol = jwtService.obtenerRol(token);

            System.out.println("DEBUG: Actualizando lista " + id + " por usuario " + nombreUsuario + " (" + rol + ")");

            ListaDTO existente = service.obtenerListaDTO(id);
            if (existente == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");

            if (!"ADMIN".equalsIgnoreCase(rol)) {
                Usuario usuarioAuth = encontrarUsuarioPorNombre(nombreUsuario);
                if (usuarioAuth == null || !existente.getUsuarioDuenoId().equals(usuarioAuth.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Solo el dueño o admin pueden actualizar esta lista");
                }
            }

            ListaDTO actualizado = service.actualizarLista(id, request);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FAIL_BACKEND: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLista(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");

        String nombreUsuario = jwtService.obtenerSubject(token);
        String rol = jwtService.obtenerRol(token);
        ListaDTO lista = service.obtenerListaDTO(id);
        if (lista == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");

        if (!rol.equals("ADMIN")) {
            if (!lista.getUsuarioDuenoId().equals(encontrarUsuarioPorNombre(nombreUsuario).getId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo el dueño o admin pueden eliminar esta lista");
        }
        boolean ok = service.eliminarLista(id);
        if (!ok)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<?> obtenerUsuariosDeLista(@PathVariable Long id) {
        ListaDTO l = service.obtenerListaDTO(id);
        if (l == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(service.obtenerUsuariosDeLista(id));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDeLista(@PathVariable Long id) {
        ListaDTO l = service.obtenerListaDTO(id);
        if (l == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        return ResponseEntity.ok(service.obtenerProductosDeLista(id));
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<?> obtenerTotalDeLista(@PathVariable Long id) {
        ListaDTO l = service.obtenerListaDTO(id);
        if (l == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista no encontrada");
        Double total = service.calcularTotalLista(id);
        return ResponseEntity.ok(total);
    }

    @PatchMapping("/{listaId}/productos/{productoId}/comprado")
    public ResponseEntity<?> cambiarEstadoComprado(
            @PathVariable Long listaId,
            @PathVariable Long productoId,
            @RequestParam boolean estado,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtService.limpiarToken(authHeader);
        if (token == null || !jwtService.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String nombreUsuario = jwtService.obtenerSubject(token);
        Usuario usuario = encontrarUsuarioPorNombre(nombreUsuario);
        if (usuario == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");

        try {
            boolean actualizado = service.marcarProductoComoComprado(listaId, productoId, estado, usuario);
            if (actualizado) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado en la lista");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    private Usuario encontrarUsuarioPorNombre(String nombre) {
        if (nombre == null)
            return null;
        Optional<Usuario> opt = repoUsuario.findAll().stream()
                .filter(u -> nombre.equals(u.getNombre()))
                .findFirst();
        return opt.orElse(null);
    }
}
