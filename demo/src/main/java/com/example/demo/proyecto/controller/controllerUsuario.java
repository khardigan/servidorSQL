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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.demo.proyecto.dto.AuthResponse;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.dto.UsuarioDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.service.serviceAuthen;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.servicePerfilUsuario;

import org.springframework.web.bind.annotation.RequestParam;
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
        String ident = request.get("nombre"); // nombre o email
        String password = request.get("password");

        if (ident == null || ident.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario o email es requerido"));
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La contraseña es requerida"));
        }

        try {
            AuthResponse authResponse = serviceAuthen.login(ident, password);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
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
        Long tokenId;
        try {
            tokenId = serviceJWT.obtenerId(token);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }

        if (tokenId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o sin ID");
        }

        String rol = serviceJWT.obtenerRol(token);

        if (!"ADMIN".equalsIgnoreCase(rol) && !tokenId.equals(id)) {
            UsuarioDTO redacted = serviceAuthen.obtenerUsuarioDTO(id);
            if (redacted != null) {
                redacted.setEmail("********@***.***");
                return ResponseEntity.ok(redacted);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UsuarioDTO u = serviceAuthen.obtenerUsuarioDTO(id);
        if (u == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(u);
    }

    // --------- Mirar Perfil --------------------
    @GetMapping("/{id}/perfil")
    public ResponseEntity<?> obtenerPerfilDelUsuario(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = validarToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        String rol = serviceJWT.obtenerRol(token);
        Long idToken = serviceJWT.obtenerId(token);

        if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(id)) {
            PerfilUsuarioDTO p = servicePerfil.obtenerPerfilPorUsuarioId(id);
            if (p != null) {
                // Devolvemos el perfil pero censurado para que el navegador no dé error 403
                p.setEmail("********@***.***");
                p.setTelefono("*********");
                return ResponseEntity.ok(p);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PerfilUsuarioDTO perfil = servicePerfil.obtenerPerfilPorUsuarioId(id);
        if (perfil == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        }
        return ResponseEntity.ok(perfil);
    }

    // ----------------- REGISTRO PÚBLICO -----------------
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPublico(
            @Valid @RequestBody CrearUsuarioRequestDTO dto) {

        serviceAuthen.crearUsuarioDesdeDTO(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensaje", "Usuario registrado. Revisa tu correo para confirmar la cuenta.",
                        "message", "Usuario registrado. Revisa tu correo para confirmar la cuenta."));
    }

    // ----------------- VERIFICAR EMAIL -----------------
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarEmail(@RequestParam String token) {
        boolean ok = serviceAuthen.verificarEmail(token);
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Correo verificado con éxito. Ya puedes iniciar sesión."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Token inválido o cuenta ya verificada"));
        }
    }

    // ----------------- ACTUALIZAR USUARIO -----------------
    // Cambia los datos de un usuario. (Tiene que recibir el ID, los datos nuevos y
    // el Token)
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> datos,
            @RequestHeader("Authorization") String authHeader) {

        String token = validarToken(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o ausente");
        }
        String rol = serviceJWT.obtenerRol(token);
        Long idToken = serviceJWT.obtenerId(token);

        // LOGS DE DEBUG PARA EL USUARIO
        System.out
                .println("PETICION ACTUALIZAR: ID destino=" + id + ", Usuario logueado ID=" + idToken + ", Rol=" + rol);

        if (!"ADMIN".equalsIgnoreCase(rol) && !idToken.equals(id)) {
            System.out.println("ACCESO DENEGADO (403): El usuario no es ADMIN ni es el dueño del perfil.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el propio usuario o admin pueden actualizar este usuario");
        }

        UsuarioDTO actualizado = serviceAuthen.actualizarUsuarioDesdeMapa(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    // ----------------- ELIMINAR USUARIO -----------------
    // Borra a un usuario. (Tiene que recibir el ID y el Token)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.joining(","));

        System.out
                .println("PETICION DELETE USUARIO: " + id + " por " + authentication.getName() + " con roles: " + rol);

        if (!rol.contains("ROLE_ADMIN") && !rol.contains("ADMIN")) {
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

        serviceAuthen.eliminarTodosLosUsuarios();
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

    // ----------------- RECUPERAR CONTRASEÑA -----------------
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String nombre = request.get("nombre");

        if (email == null || nombre == null || email.trim().isEmpty() || nombre.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nombre y email son requeridos"));
        }

        serviceAuthen.solicitarRecuperacion(nombre, email);
        return ResponseEntity.ok(Map.of("mensaje", "Si los datos coinciden, se enviará un enlace pronto"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetearPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token") != null ? request.get("token").trim() : null;
        String password = request.get("password");

        if (token == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Datos incompletos"));
        }
        // Si el nombre no es el que tiene registrado el usuario/token, no se puede
        // resetear la contraseña
        String nombre = request.get("nombre");
        if (nombre == null || !serviceAuthen.esUsuario(nombre)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nombre no registrado"));
        }

        // si el correo no es el que tiene registrado el usuario/token, no se puede
        // resetear la contraseña
        String email = request.get("email");
        if (email == null || !serviceAuthen.esUsuario(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Correo no registrado"));
        }

        boolean ok = serviceAuthen.resetearPassword(token, password);
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token inválido o caducado"));
        }
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
