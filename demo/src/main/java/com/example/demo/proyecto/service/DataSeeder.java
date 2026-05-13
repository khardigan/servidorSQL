package com.example.demo.proyecto.service;

import jakarta.transaction.Transactional;

import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Comentario;
import com.example.demo.proyecto.model.ListaProducto;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.repository.repositoryComentario;
import com.example.demo.proyecto.repository.repositoryPerfilUsuario;
import com.example.demo.proyecto.repository.repositoryListaProducto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final repositoryUsuario repoUsuario;
    private final repositoryProducto repoProducto;
    private final repositoryLista repoLista;
    private final repositoryComentario repoComentario;
    private final repositoryPerfilUsuario repoPerfil;
    private final repositoryListaProducto repoListaProducto;
    private final PasswordEncoder passwordEncoder;
    private final serviceLista sLista;

    public DataSeeder(repositoryUsuario repoUsuario,
            repositoryProducto repoProducto,
            repositoryLista repoLista,
            repositoryComentario repoComentario,
            repositoryPerfilUsuario repoPerfil,
            repositoryListaProducto repoListaProducto,
            PasswordEncoder passwordEncoder,
            serviceLista sLista) {
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
        this.repoLista = repoLista;
        this.repoComentario = repoComentario;
        this.repoPerfil = repoPerfil;
        this.repoListaProducto = repoListaProducto;
        this.passwordEncoder = passwordEncoder;
        this.sLista = sLista;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // ===================== ADMIN =====================
        Usuario admin = repoUsuario.findByEmail("linkedList@gmail.com");
        boolean perfilAdminExiste = repoPerfil.existsByNombrePerfil("Administrador");

        if (admin == null && !perfilAdminExiste) {
            admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin@gmail.com");
            admin.setContraseña(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setFechaRegistro(LocalDate.now());
            admin.setActivo(true);

            // Solo crear perfil si el admin no tiene uno (comprobando por usuario_id)
            if (admin.getPerfilUsuario() == null) {
                PerfilUsario perfil = new PerfilUsario();
                perfil.setUsuario(admin);
                perfil.setNombrePerfil("Administrador");
                perfil.setDescripcion("Usuario administrador del sistema");
                perfil.setImagenUrl("https://ui-avatars.com/api/?name=Admin&background=random");
                admin.setPerfilUsuario(perfil);
            }

            admin.setListaProductosSubidos(new ArrayList<>());
            admin.setListasCreadas(new ArrayList<>());
            admin.setListasCompartidas(new ArrayList<>());

            admin = repoUsuario.save(admin);
            System.out.println("✔ Admin creado");

            // --- Datos para Admin ---
            Lista listaAdmin = new Lista();
            listaAdmin.setNombre("Suministros Oficina");
            listaAdmin.setUsuarioDueno(admin);
            listaAdmin.setCodigo(sLista.generarCodigoAleatorio());
            repoLista.save(listaAdmin);

            Producto pAdmin = new Producto();
            pAdmin.setNombre("Monitor 4K Profesional");
            pAdmin.setPrecio(350.0);
            pAdmin.setConfirmado(true);
            pAdmin.setDescripcion("Producto importado de Amazon. Ver en: No disponible");
            pAdmin.setCategoria("Electrónica");
            pAdmin.setSupermercado("Amazon");
            pAdmin.setUsuarioRegistrador(admin);
            repoProducto.save(pAdmin);
            System.out.println("✔ Datos iniciales para Admin creados");

        } else if (admin != null) {
            if (!"ADMIN".equals(admin.getRol())) {
                admin.setRol("ADMIN");
                repoUsuario.save(admin);
                System.out.println("✔ Rol de Admin actualizado a ADMIN");
            } else {
                System.out.println("✔ Admin ya existe y tiene rol ADMIN");
            }
        }

        // ===================== USUARIO SISTEMA =====================
        Usuario listasPublicas = repoUsuario.findByEmail("listaspublicas@example.com");
        boolean perfilPublicoExiste = repoPerfil.existsByNombrePerfil("Listas Públicas");

        if (listasPublicas == null && !perfilPublicoExiste) {
            listasPublicas = new Usuario();
            listasPublicas.setNombre("ListasPublicas");
            listasPublicas.setEmail("listaspublicas@example.com");
            listasPublicas.setContraseña(passwordEncoder.encode("publicas1234"));
            listasPublicas.setRol("USER");
            listasPublicas.setFechaRegistro(LocalDate.now());
            listasPublicas.setActivo(true);

            // Solo crear perfil si no tiene uno
            if (listasPublicas.getPerfilUsuario() == null) {
                PerfilUsario perfil = new PerfilUsario();
                perfil.setUsuario(listasPublicas);
                perfil.setNombrePerfil("Listas Públicas");
                perfil.setDescripcion("Cuenta del sistema");
                perfil.setImagenUrl("https://ui-avatars.com/api/?name=Publicas&background=random");
                listasPublicas.setPerfilUsuario(perfil);
            }

            listasPublicas.setListaProductosSubidos(new ArrayList<>());
            listasPublicas.setListasCreadas(new ArrayList<>());
            listasPublicas.setListasCompartidas(new ArrayList<>());

            repoUsuario.save(listasPublicas);
            System.out.println("✔ Usuario sistema creado");
        }

        // ===================== USUARIO JOSE (CENTRALIZADO) =====================
        Usuario jose = repoUsuario.findByEmail("jose@example.com");
        if (jose == null) {
            jose = new Usuario();
            jose.setNombre("Jose");
            jose.setEmail("jose@example.com");
            jose.setRol("USER");
            jose.setFechaRegistro(LocalDate.now());
            jose.setListaProductosSubidos(new ArrayList<>());
            jose.setListasCreadas(new ArrayList<>());
            jose.setListasCompartidas(new ArrayList<>());
            System.out.println("✔ Creando nuevo usuario Jose...");
        }
        jose.setActivo(true);
        jose.setContraseña(passwordEncoder.encode("jose1234"));
        jose.setVerificationToken(null);
        jose = repoUsuario.save(jose);

        // Asegurar Perfil de Jose (Comprobar por el objeto jose, no por el nombre)
        if (jose.getPerfilUsuario() == null) {
            PerfilUsario pJ = new PerfilUsario();
            pJ.setUsuario(jose);
            pJ.setNombrePerfil("Jose");
            pJ.setEmail(jose.getEmail());
            pJ.setDescripcion("Cuenta de usuario Jose");
            pJ.setImagenUrl("https://ui-avatars.com/api/?name=Jose&background=random");
            jose.setPerfilUsuario(pJ);
            repoPerfil.save(pJ);
        }

        // Asegurar sus 4 listas (1 base + 3 públicas)
        String[] misNombresListas = { "Mi Compra Semanal", "Lista de Favoritos", "Cena de Navidad",
                "Barbacoa con Amigos" };
        List<Lista> listasActuales = repoLista.findByUsuarioDueno(jose);

        for (String nombreL : misNombresListas) {
            boolean existeL = false;
            for (Lista lExistente : listasActuales) {
                if (nombreL.equals(lExistente.getNombre())) {
                    existeL = true;
                    break;
                }
            }

            if (!existeL) {
                Lista nl = new Lista();
                nl.setNombre(nombreL);
                nl.setUsuarioDueno(jose);
                nl.setCodigo(sLista.generarCodigoAleatorio());
                nl = repoLista.save(nl);

                if (!nombreL.equals("Mi Compra Semanal")) {
                    sLista.cambiarEstadoPublicacion(nl.getCodLista(), true);
                    System.out.println("✔ Lista pública '" + nombreL + "' creada para Jose");
                } else {
                    System.out.println("✔ Lista base '" + nombreL + "' creada para Jose");
                }
            }
        }

        if (repoProducto.findByNombreContainingIgnoreCase("Miel Artesanal de la Sierra").isEmpty()) {
            Producto pP = new Producto();
            pP.setNombre("Miel Artesanal de la Sierra");
            pP.setPrecio(6.50);
            pP.setConfirmado(false);
            pP.setCategoria("Alimentación General");
            pP.setDescripcion("Producto artesanal. Ver en: No disponible");
            pP.setSupermercado("Local");
            pP.setUsuarioRegistrador(jose);
            repoProducto.save(pP);

            Comentario cP = new Comentario();
            cP.setContenido("¡Increíble sabor! Producto super natural.");
            cP.setFecha(java.sql.Date.valueOf(LocalDate.now()));
            cP.setPuntuacion(5.0);
            cP.setUsuario(jose);
            cP.setProducto(pP);
            repoComentario.save(cP);
            System.out.println("✔ Producto inicial creado para Jose");
        }

        // ===================== CARGAR PRODUCTOS EXISTENTES =====================
        logger.info("Cargando productos existentes para evitar duplicados...");
        Set<String> productosExistentes = new HashSet<>();
        List<Object[]> datosExistentes = repoProducto.findAllNombresYSupermercados();
        for (Object[] dato : datosExistentes) {
            String nE = (String) dato[0];
            String sE = (String) dato[1];
            productosExistentes.add((nE + "|" + sE).toLowerCase());
        }
        logger.info("Se han encontrado " + productosExistentes.size() + " productos en la BD.");

        // ===================== IMPORTAR PRODUCTOS (CSV) =====================
        logger.info("Importando nuevos productos desde scrapeo...");
        cargarDesdeCsv("../../scrapeo/productos_dia.csv", "Dia", productosExistentes);
        cargarDesdeCsv("../../scrapeo/productos_mercadona.csv", "Mercadona", productosExistentes);

        // ===================== RELLENAR TODAS LAS LISTAS DE JOSE SI ESTÁN VACÍAS =====================
        jose = repoUsuario.findByEmail("jose@example.com");
        if (jose != null) {
            List<Lista> todasSusListas = repoLista.findByUsuarioDueno(jose);
            List<Producto> todosLosProductos = repoProducto.findAll();

            if (!todosLosProductos.isEmpty()) {
                for (Lista lJose : todasSusListas) {
                    // Si la lista está vacía, le metemos 10 productos
                    if (lJose.getProductosEnLista() == null || lJose.getProductosEnLista().isEmpty()) {
                        int productosAnadidos = 0;
                        if (lJose.getProductosEnLista() == null)
                            lJose.setProductosEnLista(new ArrayList<>());

                        for (Producto p : todosLosProductos) {
                            // Intentamos que no sean siempre los mismos usando un pequeño offset o
                            // simplemente los primeros 10
                            // Para barbacoa o navidad podríamos filtrar, pero por ahora metemos 10
                            // genéricos para que no estén vacías
                            ListaProducto lp = new ListaProducto();
                            lp.setLista(lJose);
                            lp.setProducto(p);
                            lp.setComprado(false);
                            lp.setCantidad(1);
                            repoListaProducto.save(lp);
                            lJose.getProductosEnLista().add(lp);

                            productosAnadidos++;
                            if (productosAnadidos >= 10)
                                break;
                        }
                        repoLista.save(lJose);
                        System.out.println("✔ Lista '" + lJose.getNombre() + "' de Jose inicializada con "
                                + productosAnadidos + " productos.");
                    }
                }
            }
        }
    }

    private void cargarDesdeCsv(String csvPath, String supermercado, Set<String> productosExistentes) {
        Path archivoCSV = buscarArchivo(csvPath);
        if (archivoCSV == null || !Files.exists(archivoCSV)) {
            logger.error("No se encontró el archivo CSV de " + supermercado + ": " + csvPath);
            return;
        }

        logger.info("Importando desde: " + archivoCSV.toAbsolutePath() + " (" + supermercado + ")");
        Map<String, Producto> mapaProductos = new java.util.HashMap<>();
        int saltados = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoCSV.toFile()), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty())
                    continue;

                try {
                    String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (values.length >= 5) {
                        String nombre = values[0].trim().replace("\"", "");
                        String precioCrudo = values[1].trim().replace("\"", "");
                        String imagenUrl = values[2].trim().replace("\"", "");
                        String url = values[3].trim().replace("\"", "");
                        String categoriaCruda = values[4].trim().replace("\"", "");

                        String nombreNormalizado = nombre.length() > 100 ? nombre.substring(0, 100) : nombre;
                        String clave = (nombreNormalizado + "|" + supermercado).toLowerCase();

                        if (productosExistentes.contains(clave)) {
                            saltados++;
                            continue;
                        }

                        Producto p;
                        if (mapaProductos.containsKey(clave)) {
                            p = mapaProductos.get(clave);
                        } else {
                            p = new Producto();
                            p.setNombre(nombreNormalizado);
                            p.setSupermercado(supermercado);
                            p.setConfirmado(true);
                            p.setImagenUrl(imagenUrl.length() > 255 ? imagenUrl.substring(0, 255) : imagenUrl);
                            String urlFinal = url.isEmpty() ? "No disponible" : url;
                            String desc = "Producto importado de " + supermercado + ". Ver en: " + urlFinal;
                            p.setDescripcion(desc.length() > 300 ? desc.substring(0, 300) : desc);

                            double precioFinal = 1.99;
                            if (!precioCrudo.isEmpty()) {
                                String[] partesPrecio = precioCrudo.split(" ");
                                if (partesPrecio.length > 0) {
                                    String numStr = partesPrecio[0].replace(",", ".").replaceAll("[^0-9.]", "");
                                    try {
                                        if (!numStr.isEmpty())
                                            precioFinal = Double.parseDouble(numStr);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            p.setPrecio(precioFinal > 0 ? precioFinal : 1.99);
                            mapaProductos.put(clave, p);
                        }
                        String categoria = mapearCategoria(nombre, categoriaCruda);
                        if (!categoria.equals("General") && !categoria.isEmpty()) {
                            categoria = categoria.substring(0, 1).toUpperCase() + categoria.substring(1);
                        }
                        p.setCategoria(categoria);
                    }
                } catch (Exception e) {
                    logger.error("Error parseando línea: " + line);
                }
            }
            if (!mapaProductos.isEmpty())
                repoProducto.saveAll(mapaProductos.values());
            logger.info("Importados " + mapaProductos.size() + " de " + supermercado + ". Saltados: " + saltados);
        } catch (Exception e) {
            logger.error("Error leyendo CSV: " + e.getMessage());
        }
    }

    private Path buscarArchivo(String csvPath) {
        String workingDir = System.getProperty("user.dir");
        String nombreArchivo = csvPath.contains("/") ? csvPath.substring(csvPath.lastIndexOf("/") + 1) : csvPath;
        logger.info("Buscando archivo: " + nombreArchivo);
        Path actual = Paths.get(workingDir).normalize();
        for (int i = 0; i < 7; i++) {
            Path candidato = actual.resolve("scrapeo").resolve(nombreArchivo);
            if (Files.exists(candidato))
                return candidato;
            Path parent = actual.getParent();
            if (parent == null)
                break;
            actual = parent;
        }
        return Files.exists(Paths.get(csvPath)) ? Paths.get(csvPath) : null;
    }

    private String mapearCategoria(String nombre, String catOriginal) {
        String n = normalizar(nombre.toLowerCase());

        // PASO 1: Categoría alimentaria limpia del CSV
        if (!catOriginal.isEmpty() && !catOriginal.startsWith("http")) {
            String cN = normalizar(catOriginal.toLowerCase());
            if (contiene(cN, "carniceria", "pescaderia", "frutas", "verduras", "panaderia", "lacteos", "bebidas",
                    "congelados"))
                return catOriginal;
        }

        // PASO 2: Bloqueos (Limpieza/Cuidado)
        if (contiene(n, "limpia", "detergente", "suavizante", "lavavajillas", "lavadora", "lejia"))
            return "Limpieza y Hogar";
        if (contiene(n, "champu", "gel de", "desodorante", "colonia", "perfume", "pasta de dientes"))
            return "Cuidado Personal";

        // PASO 3: Detección por nombre
        if (contiene(n, "entrecot", "ternera", "pollo", "jamon", "salchicha", "chuleta", "lomo"))
            return "Carnicería";
        if (contiene(n, "salmon", "atun", "merluza", "gamba", "calamar", "bacalao"))
            return "Pescadería";

        // PASO 4: Categoría por URL / Crudo
        String c = normalizar(catOriginal.toLowerCase());
        if (contiene(c, "carne", "aves", "embutido"))
            return "Carnicería";
        if (contiene(c, "pescado", "marisco"))
            return "Pescadería";
        if (contiene(c, "fruta", "verdura"))
            return "Frutas y Verduras";
        if (contiene(c, "lacteo", "queso", "huevo"))
            return "Lácteos y Huevos";
        if (contiene(c, "pan", "bolleria"))
            return "Panadería";
        if (contiene(c, "bebida", "agua", "vino"))
            return "Bebidas";

        // PASO 5: Apoyo final
        if (contiene(n, "manzana", "platano", "tomate", "patata"))
            return "Frutas y Verduras";
        if (contiene(n, "leche", "yogur", "queso", "huevo"))
            return "Lácteos y Huevos";
        if (contiene(n, "chocolate", "galleta", "miel"))
            return "Dulces y Snacks";

        return (catOriginal.isEmpty() || catOriginal.startsWith("http")) ? "General" : catOriginal;
    }

    private String normalizar(String texto) {
        if (texto == null)
            return "";
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase();
    }

    private boolean contiene(String texto, String... palabras) {
        for (String p : palabras) {
            if (texto.contains(p))
                return true;
        }
        return false;
    }

}
