package com.example.demo.proyecto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.AuthResponse;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.dto.UsuarioDTO;
import com.example.demo.proyecto.exception.RecursoDuplicadoException;
import com.example.demo.proyecto.model.*;
import com.example.demo.proyecto.repository.*;

import jakarta.transaction.Transactional;

/*
 * 🔧 CAMBIOS IMPORTANTES:
 * - Fix verificación email (ya no stream, ahora repo directo)
 * - Login consistente: devuelve token o lanza excepción (no null silencioso)
 * - BuildResponse ahora es el único punto que genera JWT (evita duplicación)
 * - Eliminado setRol desde DTO (seguridad: solo USER)
 */

@Service
public class serviceAuthen {

    private final serviceJWT jwtService;
    private final repositoryProducto repoProducto;
    private final repositoryLista repoLista;
    private final repositoryUsuario repoUsuario;
    private final repositoryComentario repoComentario;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public serviceAuthen(serviceJWT jwtService,
            PasswordEncoder passwordEncoder,
            repositoryUsuario repoUsuario,
            repositoryProducto repoProducto,
            repositoryComentario repoComentario,
            repositoryLista repoLista,
            EmailService emailService) {

        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
        this.repoComentario = repoComentario;
        this.repoLista = repoLista;
        this.emailService = emailService;
    }

    // ===================== LOGIN =====================
    public AuthResponse login(String ident, String password) {

        // Buscamos por email O por nombre para que el usuario no se líe
        Usuario usuario = repoUsuario.findByEmail(ident);
        if (usuario == null) {
            usuario = repoUsuario.findByNombreIgnoreCase(ident);
        }

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("Cuenta no verificada. Por favor, revisa tu correo.");
        }

        if (!passwordEncoder.matches(password, usuario.getContraseña())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return buildResponse(usuario);
    }

    // ===================== REGISTRO / REACTIVACIÓN =====================
    @Transactional
    public AuthResponse crearUsuarioDesdeDTO(CrearUsuarioRequestDTO dto) {

        Usuario existente = repoUsuario.findByEmail(dto.getEmail());

        // 🔁 USUARIO EXISTE
        if (existente != null) {

            // 🔄 REACTIVACIÓN
            if (Boolean.FALSE.equals(existente.getActivo())) {

                existente.setNombre(dto.getNombre());
                existente.setContraseña(passwordEncoder.encode(dto.getContraseña()));

                // 🔒 SIEMPRE USER (seguridad)
                existente.setRol("USER");

                existente.setActivo(false);

                String token = UUID.randomUUID().toString();
                existente.setVerificationToken(token);

                repoUsuario.save(existente);

                emailService.enviarEmailVerificacion(
                        existente.getEmail(),
                        token,
                        existente.getNombre());

                return buildResponse(existente);
            }

            throw new RecursoDuplicadoException("Ya existe un usuario con ese email");
        }

        // 🆕 NUEVO USUARIO
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setContraseña(passwordEncoder.encode(dto.getContraseña()));

        // 🔒 SIEMPRE USER
        usuario.setRol("USER");

        usuario.setFechaRegistro(
                dto.getFechaRegistro() != null ? dto.getFechaRegistro() : LocalDate.now());

        usuario.setActivo(false);

        String token = UUID.randomUUID().toString();
        usuario.setVerificationToken(token);

        usuario.setListaProductosSubidos(new ArrayList<>());
        usuario.setListasCreadas(new ArrayList<>());
        usuario.setListasCompartidas(new ArrayList<>());

        PerfilUsario perfil = new PerfilUsario();
        perfil.setUsuario(usuario);
        usuario.setPerfilUsuario(perfil);

        // Importante: usar el objeto que devuelve save() para tener los IDs generados
        usuario = repoUsuario.save(usuario);

        emailService.enviarEmailVerificacion(usuario.getEmail(), token, usuario.getNombre());

        return buildResponse(usuario);
    }

    // ===================== VERIFICACIÓN EMAIL =====================
    @Transactional
    public boolean verificarEmail(String token) {

        Usuario usuario = repoUsuario.findByVerificationToken(token);

        if (usuario == null)
            return false;

        usuario.setActivo(true);
        usuario.setVerificationToken(null);

        repoUsuario.save(usuario);

        return true;
    }

    // ===================== DTO =====================
    public UsuarioDTO convertirAUsuarioDTO(Usuario u) {
        if (u == null)
            return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setActivo(Boolean.TRUE.equals(u.getActivo()));

        // Mapear listas de IDs para evitar errores en el frontend
        if (u.getListaProductosSubidos() != null) {
            dto.setListaProductosSubidos(u.getListaProductosSubidos().stream()
                    .map(Producto::getId).collect(Collectors.toList()));
        } else {
            dto.setListaProductosSubidos(new ArrayList<>());
        }

        if (u.getListasCreadas() != null) {
            dto.setListasCreadas(u.getListasCreadas().stream()
                    .map(Lista::getCodLista).collect(Collectors.toList()));
        } else {
            dto.setListasCreadas(new ArrayList<>());
        }

        if (u.getListasCompartidas() != null) {
            dto.setListasCompartidas(u.getListasCompartidas().stream()
                    .map(Lista::getCodLista).collect(Collectors.toList()));
        } else {
            dto.setListasCompartidas(new ArrayList<>());
        }

        return dto;
    }

    // Obtiene UsuarioDTO por ID
    public UsuarioDTO obtenerUsuarioDTO(Long id) {
        if (id == null)
            return null;
        Usuario usuario = repoUsuario.findById(id).orElse(null);
        return convertirAUsuarioDTO(usuario);
    }

    // Lista UsuarioDTO
    public List<UsuarioDTO> listarUsuariosDTO() {
        List<Usuario> usuarios = repoUsuario.findAll();
        return usuarios.stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO actualizarUsuario(Long id, Usuario usuarioDto) {
        if (id == null)
            return null;
        Usuario usuario = repoUsuario.findById(id).orElse(null);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (usuarioDto.getNombre() != null) {
            usuario.setNombre(usuarioDto.getNombre());
        }
        if (usuarioDto.getEmail() != null) {
            usuario.setEmail(usuarioDto.getEmail());
        }
        if (usuarioDto.getContraseña() != null) {
            usuario.setContraseña(passwordEncoder.encode(usuarioDto.getContraseña()));
        }
        if (usuarioDto.getRol() != null) {
            usuario.setRol(usuarioDto.getRol());
        }
        if (usuarioDto.getActivo() != null) {
            usuario.setActivo(Boolean.TRUE.equals(usuarioDto.getActivo()));
        }

        repoUsuario.save(usuario);

        return convertirAUsuarioDTO(usuario);
    }

    // Nuevo método para actualización parcial desde un Mapa (evita errores de
    // Jackson)
    public UsuarioDTO actualizarUsuarioDesdeMapa(Long id, java.util.Map<String, Object> datos) {
        Usuario usuario = repoUsuario.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // PROTECCIÓN SUPER-ADMIN
        if ("admin@gmail.com".equalsIgnoreCase(usuario.getEmail()) && datos.containsKey("rol")) {
            String nuevoRol = (String) datos.get("rol");
            if (!"ADMIN".equalsIgnoreCase(nuevoRol)) {
                throw new RuntimeException("No se puede degradar el rol del Administrador principal");
            }
        }

        if (datos.containsKey("nombre")) {
            usuario.setNombre((String) datos.get("nombre"));
        }
        if (datos.containsKey("email")) {
            if (!"admin@gmail.com".equalsIgnoreCase(usuario.getEmail())) {
                usuario.setEmail((String) datos.get("email"));
            }
        }
        if (datos.containsKey("rol")) {
            usuario.setRol((String) datos.get("rol"));
        }
        if (datos.containsKey("activo")) {
            usuario.setActivo((Boolean) datos.get("activo"));
        }
        if (datos.containsKey("contraseña")) {
            usuario.setContraseña(passwordEncoder.encode((String) datos.get("contraseña")));
        }

        repoUsuario.save(usuario);
        return convertirAUsuarioDTO(usuario);
    }

    @Transactional
    public Boolean eliminarUsuario(Long id) {
        if (id == null)
            return false;
        Usuario usuario = repoUsuario.findById(id).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 1. Limpiar colaboraciones en listas compartidas (ManyToMany)
        // Necesitamos eliminar al usuario de cada lista en la que colabora
        if (usuario.getListasCompartidas() != null) {
            for (Lista lista : new ArrayList<>(usuario.getListasCompartidas())) {
                lista.getUsuariosCompartida().remove(usuario);
                repoLista.save(lista);
            }
            usuario.getListasCompartidas().clear();
        }

        // 2. Ahora ya podemos borrar al usuario (las listas que posee se borrarán por
        // CascadeType.ALL)
        repoUsuario.delete(usuario);
        return true;
    }

    @Transactional
    public Boolean eliminarTodosLosUsuarios() {
        List<Usuario> usuarios = repoUsuario.findAll();
        if (usuarios.isEmpty()) {
            throw new RuntimeException("No hay usuarios para eliminar");
        }

        // Limpiar todas las colaboraciones en listas compartidas para todos los
        // usuarios
        List<Lista> todasLasListas = repoLista.findAll();
        for (Lista lista : todasLasListas) {
            if (lista.getUsuariosCompartida() != null) {
                lista.getUsuariosCompartida().clear();
                repoLista.save(lista);
            }
        }

        repoUsuario.deleteAll(usuarios);
        return true;
    }

    // ===================== JWT CENTRALIZADO =====================
    // esta funcion hace : recibe el usuario y construye el response que contiene el
    // token, el nombre, el email, el rol y el id
    private AuthResponse buildResponse(Usuario u) {

        String token = jwtService.generarToken(
                u.getEmail(),
                u.getRol(),
                u.getId());

        return AuthResponse.builder()
                .token(token)
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .id(u.getId())
                .build();
    }

    public AuthResponse renovarToken(String nombre, String rol, Long id) {
        if (id == null) {
            throw new RuntimeException("ID no válido para renovación");
        }
        Usuario usuario = repoUsuario.findById(id).orElse(null);

        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("Usuario no encontrado o no verificado");
        }

        return buildResponse(usuario);
    }

    public Object obtenerProductosSubidosPorUsuario(Long id) {
        if (id == null)
            return null;
        Usuario usuario = repoUsuario.findById(id).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuario.getListaProductosSubidos();
    }

    public void solicitarRecuperacion(String nombre, String email) {
        Usuario usuario = repoUsuario.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        String token = UUID.randomUUID().toString();
        usuario.setVerificationToken(token);
        repoUsuario.save(usuario);
        emailService.enviarEmailRecuperacion(email, token, nombre);
    }

    public boolean esUsuario(String ident) {
        // Buscamos coincidencia exacta para seguridad
        Usuario u1 = repoUsuario.findByEmail(ident);
        Usuario u2 = repoUsuario.findByNombreIgnoreCase(ident);
        return u1 != null || u2 != null;
    }

    public Object obtenerListasSubidosPorUsuario(Long id) {
        Usuario usuario = repoUsuario.findById(id).orElse(null);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuario.getListasCreadas();
    }

    public boolean resetearPassword(String token, String password) {
        Usuario usuario = repoUsuario.findByVerificationToken(token);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuario.setContraseña(passwordEncoder.encode(password));
        usuario.setVerificationToken(null);
        repoUsuario.save(usuario);
        return true;
    }
}