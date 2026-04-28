package com.example.demo.proyecto.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyecto.dto.AuthResponse;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.dto.UsuarioDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.service.serviceAuthen;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.servicePerfilUsuario;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class controllerUsuario {
    private final servicePerfilUsuario servicePerfil;
    private final serviceAuthen serviceAuthen;
    private final serviceJWT serviceJWT;

    public controllerUsuario(serviceAuthen serviceAuthen,
            serviceJWT serviceJWT,
            servicePerfilUsuario servicePerfil) {
        this.serviceAuthen = serviceAuthen;
        this.serviceJWT = serviceJWT;
        this.servicePerfil = servicePerfil;
    }

    // ----------------- LOGIN -----------------
    // Loguea al usuario y devuelve sus datos y el token. (Tiene que recibir nombre
    // y contraseña)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String nombre = request.get("nombre");
        String password = request.get("password");

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario es requerido"));
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La contraseña es requerida"));
        }

        AuthResponse authResponse = serviceAuthen.login(nombre, password);

        if (authResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }

        return ResponseEntity.ok(authResponse);
    }

    // ----------------- RENOVAR TOKEN -----------------
    // Te da un token nuevo si el que tienes es válido. (Tiene que recibir el token
    // actual)
    @GetMapping("/renovar")
    public ResponseEntity<AuthResponse> obtenerNuevoJWT(@RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        String nombre = serviceJWT.obtenerSubject(token);
        String rol = serviceJWT.obtenerRol(token);
        Long id = serviceJWT.obtenerId(token);
        AuthResponse nuevoToken = serviceAuthen.renovarToken(nombre, rol, id);
        return ResponseEntity.ok(nuevoToken);
    }

    // ----------------- LISTAR USUARIOS -----------------
    // Devuelve la lista de todos los usuarios.
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(serviceAuthen.listarUsuariosDTO());
    }

    // ----------------- OBTENER USUARIO POR ID (ADMIN) -----------------
    // Te da la info de un usuario por su ID. (Tiene que recibir el ID y el Token)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        Long idClaim;
        try {
            idClaim = serviceJWT.obtenerId(token); // devuelve String con el ID
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }

        if (idClaim == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o sin ID");
        }

        Long tokenId = idClaim;
        String rol = serviceJWT.obtenerRol(token);

        if (!"ADMIN".equalsIgnoreCase(rol) && !tokenId.equals(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No tienes permisos para ver este usuario");
        }

        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(u);
    }

    // --------- Mirar Perfil --------------------
    // Te da el perfil de un usuario. (Tiene que recibir el ID y el Token)
    @GetMapping("/{id}/perfil")
    public ResponseEntity<?> obtenerPerfilDelUsuario(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = validarToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }

        String rol = serviceJWT.obtenerRol(token);
        Long idToken = serviceJWT.obtenerId(token);

        if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para ver este perfil");
        }

        UsuarioDTO usuario = serviceAuthen.obtenerUsuarioDTO(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        // IMPORTANT: We must fetch the profile using the Profile ID associated with the
        // user,
        // not the User ID directly, because they might differ.
        PerfilUsuarioDTO perfil = servicePerfil.obtenerPerfilDTO(usuario.getIdPerfil());
        if (perfil == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Perfil no encontrado para este usuario");
        }

        return ResponseEntity.ok(perfil);
    }

    // ----------------- REGISTRO PÚBLICO -----------------
    // Crea un usuario nuevo y devuelve su token. (Tiene que recibir los datos del
    // usuario)
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrarPublico(@Valid @RequestBody CrearUsuarioRequestDTO dto) {
        AuthResponse authResponse = serviceAuthen.crearUsuarioDesdeDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    // ----------------- ACTUALIZAR USUARIO -----------------
    // Cambia los datos de un usuario. (Tiene que recibir el ID, los datos nuevos y
    // el Token)
    @PutMapping("actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario datos,
            @RequestHeader("Authorization") String authHeader) {

        String token = validarToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }
        String rol = serviceJWT.obtenerRol(token);
        Long idToken = serviceJWT.obtenerId(token);
        if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el propio usuario o admin pueden actualizar este usuario");
        }

        Usuario actualizado = serviceAuthen.actualizarUsuario(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    // ----------------- ELIMINAR USUARIO -----------------
    // Borra a un usuario. (Tiene que recibir el ID y el Token)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        System.out.println("PETICION RECIBIDA: DELETE /usuarios/" + id);
        String token = serviceJWT.limpiarToken(authHeader);

        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }

        String rol = serviceJWT.obtenerRol(token);
        Long idToken = serviceJWT.obtenerId(token);

        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo admin puede eliminar usuarios");
        }

        boolean ok = serviceAuthen.eliminarUsuario(id);
        if (!ok)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.noContent().build();
    }

    // ----------------- ELIMINAR TODOS LOS USUARIOS -----------------
    // Borra a todos los usuarios de la base de datos. (Tiene que recibir el Token)
    @DeleteMapping("/eliminartodos")
    public ResponseEntity<?> eliminarTodos(@RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);

        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }

        String rol = serviceJWT.obtenerRol(token);
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo admin puede eliminar usuarios");
        }

        serviceAuthen.eliminarTodosUsuarios();
        return ResponseEntity.noContent().build();
    }

    // ----------------- PRODUCTOS DEL USUARIO -----------------
    // Te da los productos que ha subido un usuario. (Tiene que recibir el ID)
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDelUsuario(@PathVariable Long id) {
        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(serviceAuthen.obtenerProductosSubidosPorUsuario(id));
    }

    // Te da las listas que ha creado un usuario. (Tiene que recibir el ID)
    @GetMapping("/{id}/listas")
    public ResponseEntity<?> obtenerListasDelUsuario(@PathVariable Long id) {
        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(serviceAuthen.obtenerListasSubidosPorUsuario(id));
    }

    // --------- Validar token
    private String validarToken(String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return null;
        }
        return token;
    }
}
