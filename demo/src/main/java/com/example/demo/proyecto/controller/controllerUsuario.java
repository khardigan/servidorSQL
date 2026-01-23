package com.example.demo.proyecto.controller;

import java.util.Map;

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

import com.example.demo.proyecto.dto.AuthResponse;
import com.example.demo.proyecto.dto.CrearUsuarioDTO;
import com.example.demo.proyecto.dto.UsuarioDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.service.serviceAuthen;
import com.example.demo.proyecto.service.serviceJWT;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuarios")
public class controllerUsuario {

    private final serviceAuthen serviceAuthen;
    private final serviceJWT serviceJWT;

    public controllerUsuario(serviceAuthen serviceAuthen, serviceJWT serviceJWT) {
        this.serviceAuthen = serviceAuthen;
        this.serviceJWT = serviceJWT;
    }

    // ----------------- LISTAR USUARIOS -----------------
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(serviceAuthen.listarUsuariosDTO());
    }

    // ----------------- LOGIN -----------------
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
    @GetMapping("/renovarJWT")
    public ResponseEntity<AuthResponse> obtenerNuevoJWT(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String nombre = serviceJWT.obtenerSubject(token);
        String rol = serviceJWT.obtenerRol(token);
        Long id = serviceJWT.obtenerId(token);
        AuthResponse nuevoToken = serviceAuthen.renovarToken(nombre, rol, id);
        return ResponseEntity.ok(nuevoToken);
    }
    // ----------------- OBTENER USUARIO POR ID (ADMIN) -----------------
  @GetMapping("id/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        Long idClaim;
        try {
            idClaim = serviceJWT.obtenerId(token); // devuelve String con el ID
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }

        if (idClaim == null ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o sin ID");
        }

        Long tokenId = Long.valueOf(idClaim); // ahora es seguro
        String rol = serviceJWT.obtenerRol(token);

        if(!rol.equals("ADMIN") && !tokenId.equals(id)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("No tienes permisos para ver este usuario");
        }

        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(u);
    }



    // ----------------- REGISTRO PÚBLICO -----------------
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrarPublico(@Valid @RequestBody CrearUsuarioDTO dto) {
        AuthResponse authResponse = serviceAuthen.crearUsuarioDesdeDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    // ----------------- ACTUALIZAR USUARIO -----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario datos, 
                                        @RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }

        String rol = serviceJWT.obtenerRol(token);
        String nombreUsuario = serviceJWT.obtenerSubject(token);

        UsuarioDTO existente = serviceAuthen.obtenerUsuarioDTO(id);
        if (existente == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");

        if (!rol.equals("ADMIN") && !existente.getNombre().equals(nombreUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el propio usuario o admin pueden actualizar este usuario");
        }

        Usuario actualizado = serviceAuthen.actualizarUsuario(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    // ----------------- ELIMINAR USUARIO -----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }
        String rol = serviceJWT.obtenerRol(token);

        if (!rol.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo admin puede eliminar usuarios");
        }

        boolean ok = serviceAuthen.eliminarUsuario(id);
        if (!ok) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.noContent().build();
    }
    
    
    // ----------------- ELIMINAR TODOS LOS USUARIOS -----------------
    @DeleteMapping("/todos")
    public ResponseEntity<?> eliminarTodos(@RequestHeader("Authorization") String authHeader) {
        String token = serviceJWT.limpiarToken(authHeader);
        if (token == null || !serviceJWT.esTokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ausente");
        }
    
        String rol = serviceJWT.obtenerRol(token);
        if (!rol.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo admin puede eliminar usuarios");
        }
        
        serviceAuthen.eliminarTodosUsuarios();
        return ResponseEntity.noContent().build();
    }
    
    // ----------------- PRODUCTOS DEL USUARIO -----------------
    @GetMapping("/{id}/productos")
    public ResponseEntity<?> obtenerProductosDelUsuario(@PathVariable Long id) {
        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(serviceAuthen.obtenerProductosSubidosPorUsuario(id));
    }
    
    
    @GetMapping("/{id}/listas")
    public ResponseEntity<?> obtenerListasDelUsuario(@PathVariable Long id) {
        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(serviceAuthen.obtenerListasSubidosPorUsuario(id));
    }
    
}
