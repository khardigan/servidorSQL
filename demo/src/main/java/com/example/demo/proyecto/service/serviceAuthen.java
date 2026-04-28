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

import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.ListaProducto;
import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.repository.repositoryComentario;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class serviceAuthen {

    private final serviceJWT jwtService;
    private final repositoryProducto repoProducto;
    private final repositoryLista repoLista;
    private final repositoryUsuario repoUsuario;
    private final repositoryComentario repoComentario;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        // ===================== USUARIOS =====================
        boolean adminExiste = repoUsuario.findAll().stream()
                .anyMatch(u -> u.getNombre().equals("admin"));

        Usuario admin;
        Usuario usuario2;
        Usuario usuario3;

        if (!adminExiste) {
            // ---------- Usuario 1: admin ----------
            admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin@example.com");
            admin.setContraseña(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setFechaRegistro(LocalDate.now());
            admin.setListaProductosSubidos(new ArrayList<>());
            admin.setListasCreadas(new ArrayList<>());
            admin.setListasCompartidas(new ArrayList<>());

            PerfilUsario perfilAdmin = new PerfilUsario();
            perfilAdmin.setUsuario(admin);
            perfilAdmin.setNombrePerfil("Administrador");
            perfilAdmin.setDescripcion("Perfil del usuario administrador");
            admin.setPerfilUsuario(perfilAdmin);

            admin = repoUsuario.save(admin);
            System.out.println("Usuario admin creado con éxito.");

            // ---------- Usuario 2: usuario2 ----------
            usuario2 = new Usuario();
            usuario2.setNombre("usuario2");
            usuario2.setEmail("usuario2@example.com");
            usuario2.setContraseña(passwordEncoder.encode("user1234"));
            usuario2.setRol("USER");
            usuario2.setFechaRegistro(LocalDate.now());
            usuario2.setListaProductosSubidos(new ArrayList<>());
            usuario2.setListasCreadas(new ArrayList<>());
            usuario2.setListasCompartidas(new ArrayList<>());

            PerfilUsario perfilUser2 = new PerfilUsario();
            perfilUser2.setUsuario(usuario2);
            perfilUser2.setNombrePerfil("UsuarioDos");
            perfilUser2.setDescripcion("Perfil del segundo usuario de prueba");
            usuario2.setPerfilUsuario(perfilUser2);

            usuario2 = repoUsuario.save(usuario2);
            System.out.println("Usuario usuario2 creado con éxito.");

            // ---------- Usuario 3: usuario3 ----------
            usuario3 = new Usuario();
            usuario3.setNombre("usuario3");
            usuario3.setEmail("usuario3@example.com");
            usuario3.setContraseña(passwordEncoder.encode("user1234"));
            usuario3.setRol("USER");
            usuario3.setFechaRegistro(LocalDate.now());
            usuario3.setListaProductosSubidos(new ArrayList<>());
            usuario3.setListasCreadas(new ArrayList<>());
            usuario2.setListasCompartidas(new ArrayList<>());

            PerfilUsario perfilUser3 = new PerfilUsario();
            perfilUser3.setUsuario(usuario3);
            perfilUser3.setNombrePerfil("UsuarioTres");
            perfilUser3.setDescripcion("Perfil del tercer usuario de prueba");
            usuario3.setPerfilUsuario(perfilUser3);

            usuario3 = repoUsuario.save(usuario3);
            System.out.println("Usuario usuario3 creado con éxito.");

            // ===================== PRODUCTOS =====================
            Producto leche = new Producto();
            leche.setNombre("Leche entera");
            leche.setDescripcion("Brick de leche entera 1L");
            leche.setPrecio(1.20);
            leche.setConfirmado(true);
            leche.setUsuarioRegistrador(admin);
            leche = repoProducto.save(leche);

            Producto pan = new Producto();
            pan.setNombre("Pan integral");
            pan.setDescripcion("Barra de pan integral de 500g");
            pan.setPrecio(1.50);
            pan.setConfirmado(true);
            pan.setUsuarioRegistrador(admin);
            pan = repoProducto.save(pan);

            Producto huevos = new Producto();
            huevos.setNombre("Huevos camperos");
            huevos.setDescripcion("Docena de huevos camperos frescos");
            huevos.setPrecio(2.80);
            huevos.setConfirmado(true);
            huevos.setUsuarioRegistrador(usuario2);
            huevos = repoProducto.save(huevos);

            Producto arroz = new Producto();
            arroz.setNombre("Arroz basmati");
            arroz.setDescripcion("Paquete de arroz basmati 1kg");
            arroz.setPrecio(2.10);
            arroz.setConfirmado(true);
            arroz.setUsuarioRegistrador(admin);
            arroz = repoProducto.save(arroz);

            System.out.println("Productos de prueba creados con éxito.");

            // ===================== LISTAS =====================

            // ---------- Lista 1: Compartida (admin dueño, usuario2 invitado) ----------
            Lista listaCompartida = new Lista();
            listaCompartida.setCodLista(1L);
            listaCompartida.setUsuarioDueno(admin);
            listaCompartida.setUsuariosCompartida(List.of(usuario2));

            // Productos en la lista compartida: leche, pan, huevos
            ListaProducto lp1 = new ListaProducto();
            lp1.setLista(listaCompartida);
            lp1.setProducto(leche);
            lp1.setComprado(false);

            ListaProducto lp2 = new ListaProducto();
            lp2.setLista(listaCompartida);
            lp2.setProducto(pan);
            lp2.setComprado(true); // Este ya está comprado

            ListaProducto lp3 = new ListaProducto();
            lp3.setLista(listaCompartida);
            lp3.setProducto(huevos);
            lp3.setComprado(false);

            listaCompartida.setProductosEnLista(List.of(lp1, lp2, lp3));
            repoLista.save(listaCompartida);
            System.out.println("Lista 1 (compartida) creada con éxito.");

            // ---------- Lista 2: Privada (solo admin) ----------
            Lista listaPrivada = new Lista();
            listaPrivada.setCodLista(2L);
            listaPrivada.setUsuarioDueno(admin);
            listaPrivada.setUsuariosCompartida(new ArrayList<>());

            // Productos en la lista privada: arroz
            ListaProducto lp4 = new ListaProducto();
            lp4.setLista(listaPrivada);
            lp4.setProducto(arroz);
            lp4.setComprado(false);

            listaPrivada.setProductosEnLista(List.of(lp4));
            repoLista.save(listaPrivada);
            System.out.println("Lista 2 (privada de admin) creada con éxito.");

        } else {
            // Si admin ya existe, solo actualizamos su contraseña
            repoUsuario.findAll().stream()
                    .filter(u -> u.getNombre().equals("admin"))
                    .findFirst()
                    .ifPresent(a -> {
                        a.setContraseña(passwordEncoder.encode("admin123"));
                        repoUsuario.save(a);
                    });
            System.out.println("Usuario admin ya existe. Datos de prueba no recreados.");
        }
    }

    public serviceAuthen(serviceJWT jwtService, PasswordEncoder passwordEncoder,
            repositoryUsuario repoUsuario,
            repositoryProducto repoProducto,
            repositoryComentario repoComentario,
            repositoryLista repoLista) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
        this.repoComentario = repoComentario;
        this.repoLista = repoLista;
    }

    // ----------------- CRUD Usuarios -----------------

    // Devuelve la lista de todos los usuarios activos en formato DTO.
    public List<UsuarioDTO> listarUsuariosDTO() {
        List<Usuario> usuarios = repoUsuario.findAll();
        return usuarios.stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    // Comprueba el usuario y la contraseña para dejarle entrar.
    public AuthResponse login(String nombre, String password) {
        Usuario usuario = repoUsuario.findAll().stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            System.out.println("Usuario no encontrado: " + nombre);
            return null;
        }

        if (!passwordEncoder.matches(password, usuario.getContraseña())) {
            System.out.println("Password incorrecta para: " + nombre);
            return null;
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            System.out.println("Usuario desactivado: " + nombre);
            return null;
        }

        // Pasa el id del usuario al token
        String token = jwtService.generarToken(nombre, usuario.getRol(), usuario.getId());
        return AuthResponse.builder().token(token).nombre(nombre).rol(usuario.getRol()).id(usuario.getId()).build();
    }

    // Te da la información de un usuario por su ID.
    public UsuarioDTO obtenerUsuarioDTO(Long id) {
        Usuario u = repoUsuario.findById(id).orElse(null);
        return convertirAUsuarioDTO(u);
    }

    // Guarda un usuario nuevo cifrando su contraseña.
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getNombre() != null) {
            boolean existe = repoUsuario.findAll().stream()
                    .anyMatch(u -> u.getNombre() != null && u.getNombre().equals(usuario.getNombre()));
            if (existe) {
                throw new com.example.demo.proyecto.exception.RecursoDuplicadoException("Usuario ya existe");
            }
        }

        if (usuario.getContraseña() != null) {
            String passwordCifrada = passwordEncoder.encode(usuario.getContraseña());
            usuario.setContraseña(passwordCifrada);
        }

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

    // Aqui cogemos y creamos a partir del json un usuario dto y luego lo pasamos a
    // usuario origina
    @Transactional
    // Registra un usuario nuevo desde los datos del formulario.
    public AuthResponse crearUsuarioDesdeDTO(CrearUsuarioRequestDTO dto) {
        // Validación de duplicados
        boolean existe = repoUsuario.findAll().stream()
                .anyMatch(u -> u.getNombre() != null && u.getNombre().equals(dto.getNombre()));
        if (existe) {
            throw new com.example.demo.proyecto.exception.RecursoDuplicadoException(
                    "Usuario ya existe en la base de datos");
        }

        String passwordHasheada = passwordEncoder.encode(dto.getContraseña());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setContraseña(passwordHasheada);
        usuario.setRol(dto.getRol());
        usuario.setFechaRegistro(dto.getFechaRegistro() != null ? dto.getFechaRegistro() : LocalDate.now());
        usuario.setActivo(true);

        // Inicializamos relaciones vacías
        usuario.setListaProductosSubidos(new ArrayList<>());
        usuario.setListasCreadas(new ArrayList<>());
        usuario.setListasCompartidas(new ArrayList<>());
        PerfilUsario perfil = new PerfilUsario();
        perfil.setUsuario(usuario); // enlace bidireccional
        usuario.setPerfilUsuario(perfil);

        repoUsuario.save(usuario); // Guardamos el usuario primero

        String token = jwtService.generarToken(usuario.getNombre(), usuario.getRol(), usuario.getId());
        return AuthResponse.builder().token(token).nombre(usuario.getNombre()).rol(usuario.getRol()).id(usuario.getId())
                .build();
    }

    // Actualiza los datos de un usuario.
    public Usuario actualizarUsuario(Long id, Usuario datos) {
        datos.setId(id);
        return repoUsuario.save(datos);
    }

    // Desactiva un usuario (borrado lógico) por su ID.
    @Transactional
    public boolean eliminarUsuario(Long id) {
        System.out.println("DEBUG: Iniciando borrado lógico para usuario ID: " + id);
        Usuario usuario = repoUsuario.findById(id).orElse(null);
        if (usuario == null) {
            System.out.println("DEBUG: Usuario no encontrado en BD.");
            return false;
        }

        System.out.println(
                "DEBUG: Usuario '" + usuario.getNombre() + "' encontrado. Activo antes: " + usuario.getActivo());

        // Borrado lógico: desactivamos al usuario en lugar de borrarlo físicamente
        usuario.setActivo(false);
        repoUsuario.save(usuario);

        System.out.println("DEBUG: Usuario ID " + id + " guardado con activo = false.");
        return true;
    }

    // Desactiva a todos los usuarios del sistema.
    public void eliminarTodosUsuarios() {
        System.out.println("DEBUG: Iniciando desactivación masiva de todos los usuarios.");
        List<Usuario> usuarios = repoUsuario.findAll();
        for (Usuario u : usuarios) {
            u.setActivo(false);
        }
        repoUsuario.saveAll(usuarios);
        System.out.println("DEBUG: " + usuarios.size() + " usuarios desactivados.");
    }

    // Te da los productos que ha subido un usuario específico.
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

    // Crea un nuevo token con los mismos datos del usuario.
    public AuthResponse renovarToken(String nombre, String rol, Long id) {
        String token = jwtService.generarToken(nombre, rol, id);
        return AuthResponse.builder().token(token).nombre(nombre).rol(rol).id(id).build();
    }

    // ----------------- Conversión a DTO -----------------
    // Pasa los datos del usuario del modelo al formato DTO.
    public UsuarioDTO convertirAUsuarioDTO(Usuario u) {
        if (u == null)
            return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setFechaRegistro(u.getFechaRegistro() != null ? u.getFechaRegistro().toString() : null);
        dto.setActivo(Boolean.TRUE.equals(u.getActivo()));

        dto.setListaProductosSubidos(
                u.getListaProductosSubidos() != null
                        ? u.getListaProductosSubidos().stream()
                                .map(Producto::getId)
                                .sorted()
                                .collect(Collectors.toList())
                        : List.of());
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
                        : List.of());
        dto.setListasCompartidas(
                u.getListasCompartidas() != null
                        ? u.getListasCompartidas().stream()
                                .map(l -> l.getCodLista())
                                .sorted() // <-- orden ascendente
                                .collect(Collectors.toList())
                        : List.of());

        return dto;
    }

}
