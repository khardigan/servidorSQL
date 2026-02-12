package com.example.demo.proyecto.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.AuthResponse;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.dto.UsuarioDTO;
import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class serviceAuthen {

    private final serviceJWT jwtService;
    private final repositoryUsuario repoUsuario;
    private final repositoryProducto repoProducto;
    private final repositoryLista repoLista;
    private final PasswordEncoder passwordEncoder;



    // Usuarios → rol 
    private final Map<String, String> roles = Map.of(
        "admin", "ADMIN",
        "juan", "USER",
        "jose", "DISTRIBUTOR"
    );

    @PostConstruct
    public void init() {
        // Verifica si ya existe el usuario admin
        boolean adminExiste = repoUsuario.findAll().stream()
                .anyMatch(u -> u.getNombre().equals("admin"));

        if (!adminExiste) {
            // Crear admin con contraseña encriptada
            Usuario admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin@example.com");
            admin.setContraseña(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setFechaRegistro(LocalDate.now());

            // Inicializar listas y perfil
            admin.setListaProductosSubidos(new ArrayList<>());
            admin.setListasCreadas(new ArrayList<>());
            admin.setListasCompartidas(new ArrayList<>());

            PerfilUsario perfil = new PerfilUsario();
            perfil.setUsuario(admin);
            perfil.setNombrePerfil("Administrador"); // O cualquier valor válido
            perfil.setDescripcion("Perfil del usuario administrador"); // O cualquier valor válido
            admin.setPerfilUsuario(perfil);

            repoUsuario.save(admin);

            repoUsuario.save(admin);
            System.out.println("Usuario admin creado con éxito.");
        } else {
            System.out.println("Usuario admin ya existe, no se creó de nuevo.");
        }
    }

    public serviceAuthen(serviceJWT jwtService,PasswordEncoder passwordEncoder,
                         repositoryUsuario repoUsuario,
                         repositoryProducto repoProducto,
                         repositoryLista repoLista) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
        this.repoLista = repoLista;
    }

    // ----------------- CRUD Usuarios -----------------

    public List<UsuarioDTO> listarUsuariosDTO() {
        List<Usuario> usuarios = repoUsuario.findAll();
        return usuarios.stream().map(this::convertirAUsuarioDTO).collect(Collectors.toList());
    }

    public AuthResponse login(String nombre, String password) {
        Usuario usuario = repoUsuario.findAll().stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
        System.out.println(passwordEncoder.matches(password, usuario.getContraseña()));
        if (usuario == null || !passwordEncoder.matches(password, usuario.getContraseña())) {
            return null;
        }

        //Pasa el id del usuario al token
        String token = jwtService.generarToken(nombre, usuario.getRol(), usuario.getId());
        return AuthResponse.builder().token(token).nombre(nombre).rol(usuario.getRol()).build();
    }

    public UsuarioDTO obtenerUsuarioDTO(Long id) {
        Usuario u = repoUsuario.findById(id).orElse(null);
        return convertirAUsuarioDTO(u);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getNombre() != null) {
            boolean existe = repoUsuario.findAll().stream()
                    .anyMatch(u -> u.getNombre() != null && u.getNombre().equals(usuario.getNombre()));
            if (existe) {
                throw new com.example.demo.proyecto.exception.RecursoDuplicadoException("Usuario ya existe en la base de datos");
            }
        }

        
        // Inicializar objetos y listas vacías si vienen null
        if (usuario.getPerfilUsuario() == null) {
            usuario.setPerfilUsuario(new PerfilUsario());
        }
        if (usuario.getListaProductosSubidos() == null) {
            usuario.setListaProductosSubidos(new ArrayList<>());
        }
        if (usuario.getListasCreadas() == null) {
            usuario.setListasCreadas(new ArrayList<>());
        }
        if (usuario.getListasCompartidas() == null) {
            usuario.setListasCompartidas(new ArrayList<>());
        }

        return repoUsuario.save(usuario);
    }
    //Aqui cogemos y creamos a partir del json un usuario dto y luego lo pasamos a usuario origina
    @Transactional
    public AuthResponse crearUsuarioDesdeDTO(CrearUsuarioRequestDTO dto) {
        // Validación de duplicados
        boolean existe = repoUsuario.findAll().stream()
                .anyMatch(u -> u.getNombre() != null && u.getNombre().equals(dto.getNombre()));
        if (existe) {
            throw new com.example.demo.proyecto.exception.RecursoDuplicadoException("Usuario ya existe en la base de datos");
        }

        String passwordHasheada = passwordEncoder.encode(dto.getContraseña());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setContraseña(passwordHasheada);
        usuario.setRol(dto.getRol());
        usuario.setFechaRegistro(dto.getFechaRegistro() != null ? dto.getFechaRegistro() : LocalDate.now());

        // Inicializamos relaciones vacías
        usuario.setListaProductosSubidos(new ArrayList<>());
        usuario.setListasCreadas(new ArrayList<>());
        usuario.setListasCompartidas(new ArrayList<>());
        PerfilUsario perfil = new PerfilUsario();
        perfil.setUsuario(usuario); // enlace bidireccional
        usuario.setPerfilUsuario(perfil);

        repoUsuario.save(usuario); // Guardamos el usuario primero

        String token = jwtService.generarToken(usuario.getNombre(), usuario.getRol(), usuario.getId());
        return AuthResponse.builder().token(token).nombre(usuario.getNombre()).rol(usuario.getRol()).build();
    }


    public Usuario actualizarUsuario(Long id, Usuario datos) {
        datos.setId(id);
        return repoUsuario.save(datos);
    }

    public boolean eliminarUsuario(Long id) {
        if (repoUsuario.existsById(id)) {
            repoUsuario.deleteById(id);
            return true;
        }
        return false;
    }

    public void eliminarTodosUsuarios() {
        repoLista.deleteAll();      // borra listas y relaciones
        repoProducto.deleteAll();   // borra productos
        repoUsuario.deleteAll();    // borra usuarios
    }

    public List<Producto> obtenerProductosSubidosPorUsuario(Long id) {
        Usuario u = repoUsuario.findById(id).orElse(null);
        return u == null ? new ArrayList<>() : u.getListaProductosSubidos();
    }
     public List<? extends Object> obtenerListasSubidosPorUsuario(Long id) {
        Usuario u = repoUsuario.findById(id).orElse(null);
        return u == null ? new ArrayList<>() : u.getListasCompartidas();
    }

    public String verUsuarios(String nombreUsuario) {
        return repoUsuario.findAll().stream()
                .filter(u -> u.getNombre().equals(nombreUsuario))
                .map(Usuario::getNombre)
                .findFirst()
                .orElse("Usuario no encontrado");
    }

    public void registrarUsuario(Usuario usuario) {
        repoUsuario.save(usuario);
    }

    public AuthResponse renovarToken(String nombre, String rol, Long id) {
        String token = jwtService.generarToken(nombre, rol, id);
        return AuthResponse.builder().token(token).nombre(nombre).rol(rol).build();
    }

    // ----------------- Conversión a DTO -----------------
   public UsuarioDTO convertirAUsuarioDTO(Usuario u) {
    if (u == null) return null;

    UsuarioDTO dto = new UsuarioDTO();
    dto.setId(u.getId());
    dto.setNombre(u.getNombre());
    dto.setEmail(u.getEmail()); 
    dto.setRol(u.getRol());
    dto.setFechaRegistro(u.getFechaRegistro() != null ? u.getFechaRegistro().toString() : null);

    dto.setListaProductosSubidos(
        u.getListaProductosSubidos() != null
            ? u.getListaProductosSubidos().stream()
                .map(Producto::getId)
                .sorted()
                .collect(Collectors.toList())
            : List.of()
    );
    int id_perfil = 0;
    if (u.getPerfilUsuario() != null) {
        PerfilUsario perfil = u.getPerfilUsuario();
        id_perfil = perfil.getId();
    }
    dto.setIdPerfil(id_perfil);
    dto.setListasCreadas(
        u.getListasCreadas() != null
            ? u.getListasCreadas().stream()
                .map(l -> l.getCodLista())
                .sorted() // <-- orden ascendente
                .collect(Collectors.toList())
            : List.of()
    );
    dto.setListasCompartidas(
        u.getListasCompartidas() != null
            ? u.getListasCompartidas().stream()
                .map(l -> l.getCodLista())
                .sorted() // <-- orden ascendente
                .collect(Collectors.toList())
            : List.of()
    );

    return dto;
    }

}
