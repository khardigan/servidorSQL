package com.example.demo.proyecto.service;

import jakarta.transaction.Transactional;

import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

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
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final repositoryUsuario repoUsuario;
    private final repositoryProducto repoProducto;
    private final repositoryLista repoLista;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(repositoryUsuario repoUsuario,
            repositoryProducto repoProducto,
            repositoryLista repoLista,
            PasswordEncoder passwordEncoder) {
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
        this.repoLista = repoLista;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // ===================== ADMIN =====================
        Usuario admin = repoUsuario.findByEmail("admin@example.com");

        if (admin == null) {
            admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin@example.com");
            admin.setContraseña(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setFechaRegistro(LocalDate.now());
            admin.setActivo(true);

            PerfilUsario perfil = new PerfilUsario();
            perfil.setUsuario(admin);
            perfil.setNombrePerfil("Administrador");
            perfil.setDescripcion("Usuario administrador del sistema");

            admin.setPerfilUsuario(perfil);
            admin.setListaProductosSubidos(new ArrayList<>());
            admin.setListasCreadas(new ArrayList<>());
            admin.setListasCompartidas(new ArrayList<>());

            admin = repoUsuario.save(admin);
            System.out.println("✔ Admin creado");
        } else {
            System.out.println("✔ Admin ya existe");
        }

        // ===================== USUARIO SISTEMA =====================
        Usuario listasPublicas = repoUsuario.findByEmail("listaspublicas@example.com");

        if (listasPublicas == null) {
            listasPublicas = new Usuario();
            listasPublicas.setNombre("ListasPublicas");
            listasPublicas.setEmail("listaspublicas@example.com");
            listasPublicas.setContraseña(passwordEncoder.encode("publicas1234"));
            listasPublicas.setRol("USER");
            listasPublicas.setFechaRegistro(LocalDate.now());
            listasPublicas.setActivo(true);

            PerfilUsario perfil = new PerfilUsario();
            perfil.setUsuario(listasPublicas);
            perfil.setNombrePerfil("Listas Públicas");
            perfil.setDescripcion("Cuenta del sistema");

            listasPublicas.setPerfilUsuario(perfil);
            listasPublicas.setListaProductosSubidos(new ArrayList<>());
            listasPublicas.setListasCreadas(new ArrayList<>());
            listasPublicas.setListasCompartidas(new ArrayList<>());

            repoUsuario.save(listasPublicas);

            System.out.println("✔ Usuario sistema creado");
        }

        // ===================== LIMPIEZA + CSV =====================
        logger.info("Limpiando base de datos...");
        try {
            repoProducto.limpiarTablasHuerfanas();
            repoProducto.deleteAll();
            logger.info("OK limpieza");
        } catch (Exception e) {
            logger.warn("Aviso limpieza: " + e.getMessage());
        }

        logger.info("Importando productos...");
        cargarDesdeCsv("../../scrapeo/productos_dia.csv", "Dia");
        cargarDesdeCsv("../../scrapeo/productos_mercadona.csv", "Mercadona");
    }

    private void cargarDesdeCsv(String csvPath, String supermercado) {
        // Buscar el archivo en diferentes ubicaciones posibles
        Path archivoCSV = buscarArchivo(csvPath);

        if (archivoCSV == null || !Files.exists(archivoCSV)) {
            logger.error("No se encontró el archivo CSV de " + supermercado + ": " + csvPath);
            return;
        }

        logger.info("Importando desde: " + archivoCSV.toAbsolutePath() + " (" + supermercado + ")");

        // 🔥 LIMPIEZA: Borrar productos antiguos de este supermercado para que solo
        // queden los nuevos
        repoProducto.deleteBySupermercado(supermercado);

        Map<String, Producto> mapaProductos = new java.util.HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivoCSV.toFile()), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Saltar la cabecera
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    // Separar por comas que no estén dentro de comillas
                    String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (values.length >= 5) {
                        String nombre = values[0].trim().replace("\"", "");
                        String precioCrudo = values[1].trim().replace("\"", "");
                        String imagenUrl = values[2].trim().replace("\"", "");
                        String url = values[3].trim().replace("\"", "");
                        String categoriaCruda = values[4].trim().replace("\"", "");

                        String nombreNormalizado = nombre.length() > 100 ? nombre.substring(0, 100) : nombre;
                        String clave = (nombreNormalizado + "|" + supermercado).toLowerCase();

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

                            // Parsear precio
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

                        // La categoría ahora viene limpia desde el CSV (columna 4)
                        String catOriginal = values.length > 4 ? values[4].replace("\"", "").trim() : "General";

                        String categoria = mapearCategoria(nombre, catOriginal);
                        if (!categoria.equals("General") && !categoria.isEmpty()) {
                            categoria = categoria.substring(0, 1).toUpperCase() + categoria.substring(1);
                        }
                        p.setCategoria(categoria);
                        if (nombre.toLowerCase().contains("entrecot")) {
                            logger.info("DEBUG: Producto '{}' categorizado como '{}' (Origen: {})", nombre, categoria,
                                    catOriginal);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error al parsear la línea: " + line, e);
                }
            }

            if (!mapaProductos.isEmpty()) {
                repoProducto.saveAll(mapaProductos.values());
                logger.info("¡Se han importado " + mapaProductos.size() + " productos únicos de " + supermercado + "!");
            }

        } catch (Exception e) {
            logger.error("Error al leer el archivo CSV de " + supermercado + ": " + e.getMessage());
        }
    }

    /**
     * Busca el archivo CSV en múltiples ubicaciones posibles
     */
    private Path buscarArchivo(String csvPath) {
        String workingDir = System.getProperty("user.dir");
        String nombreArchivo = csvPath.contains("/") ? csvPath.substring(csvPath.lastIndexOf("/") + 1) : csvPath;

        logger.info("Buscando archivo: " + nombreArchivo);
        logger.info("Working dir actual: " + workingDir);

        // Estrategia 1: Subir desde el working dir actual hasta encontrar la carpeta
        // 'scrapeo'
        Path actual = Paths.get(workingDir).normalize();

        for (int i = 0; i < 7; i++) {
            Path candidato = actual.resolve("scrapeo").resolve(nombreArchivo);
            logger.debug("  Intento " + (i + 1) + ": " + candidato.toAbsolutePath());
            if (Files.exists(candidato)) {
                logger.info("✓ Archivo encontrado en: " + candidato.toAbsolutePath());
                return candidato;
            }

            Path parent = actual.getParent();
            if (parent == null)
                break;
            actual = parent;
        }

        // Estrategia 2: Intentar con la ruta original por si es una ruta absoluta
        logger.debug("  Intento final (ruta original): " + Paths.get(csvPath).toAbsolutePath());
        if (Files.exists(Paths.get(csvPath))) {
            return Paths.get(csvPath);
        }

        logger.error("No se pudo encontrar el archivo después de intentar 7 niveles hacia arriba");
        return null;
    }

    private String mapearCategoria(String nombre, String catOriginal) {
        String n = normalizar(nombre.toLowerCase());

        // =========================
        // ✅ PASO 1: Si el CSV ya nos da una categoría ALIMENTARIA limpia, confiar en
        // ella directamente.
        // Esto evita que los filtros de nombre reclasifiquen productos correctos.
        // =========================
        if (!catOriginal.isEmpty() && !catOriginal.startsWith("http")) {
            String catNorm = normalizar(catOriginal.toLowerCase());
            if (contiene(catNorm, "carniceria", "pescaderia", "frutas", "verduras", "panaderia",
                    "lacteos", "bebidas", "congelados")) {
                return catOriginal; // La categoría del CSV es de confianza
            }
        }

        // =========================
        // 🚨 PASO 2: BLOQUEOS — Solo para productos sin categoría alimentaria clara
        // =========================
        if (contiene(n, "limpia", "detergente", "suavizante", "lavavajillas", "lavadora", "friegasuelos",
                "desinfectante",
                "estropajo", "bayeta", "multiusos", "antical", "ambientador", "spray limpia", "tejidos", "insecticida",
                "lavavajilla", "quitagrasa", "lejia")) {
            return "Limpieza y Hogar";
        }

        // ⚠️ "gel de" en vez de "gel" para no matchear "congelado/ultracongelado"
        if (contiene(n, "laca de", "unas", "champu", "gel de", "desodorante", "colonia", "perfume", "cepillo dental",
                "pasta de dientes", "maquillaje", "aftersun", "locion corporal", "bronceador", "leche corporal",
                "crema solar", "crema hidratante", "protector solar", "dentifrico", "enjuague bucal",
                "cuchilla de afeitar", "toallitas desmaq")) {
            return "Cuidado Personal";
        }

        // Si el CSV da una categoría legible (no URL), la respetamos
        if (!catOriginal.isEmpty() && !catOriginal.startsWith("http")) {
            return catOriginal;
        }

        String c = normalizar(catOriginal.toLowerCase());

        // =========================
        // 🥩 PASO 3: DETECCIÓN POR NOMBRE (Solo si no hay categoría CSV)
        // =========================
        if (contiene(n, "entrecot", "chuleton", "ternera", "vacuno", "angus", "chulet", "filete", "lomo", "hamburguesa",
                "salchicha", "jamon", "pollo", "pavo", "cerdo", "iberico", "conejo", "cordero", "costilla", "picada",
                "panceta", "bacon", "chorizo", "fuet", "salami")) {
            return "Carnicería";
        }

        if (contiene(n, "salmon", "atun", "merluza", "bacalao", "pulpo", "gamba", "langostino", "dorada", "lubina",
                "chipiron", "calamar", "mejillon", "almeja", "trucha", "sardina", "boqueron")) {
            return "Pescadería";
        }

        // =========================
        // 🔝 PASO 4: CATEGORÍA POR URL / CSV CRUDO
        // =========================
        if (contiene(c, "carne", "pollo", "aves", "vacuno", "cerdo", "embutido", "charcuteria"))
            return "Carnicería";

        if (contiene(c, "pescado", "marisco", "molusco"))
            return "Pescadería";

        if (contiene(c, "fruta", "verdura", "hortaliza", "ensalada"))
            return "Frutas y Verduras";

        if (contiene(c, "leche", "lacteo", "queso", "huevo", "yogur"))
            return "Lácteos y Huevos";

        if (contiene(c, "pan", "bolleria", "pasteleria", "horno"))
            return "Panadería";

        if (contiene(c, "bebida", "agua", "bodega", "vino", "cerveza", "refresco", "zumo"))
            return "Bebidas";

        if (contiene(c, "limpieza", "hogar", "detergente"))
            return "Limpieza y Hogar";

        if (contiene(c, "perfumeria", "higiene", "cuidado", "cosmetica"))
            return "Cuidado Personal";

        if (contiene(c, "congelado"))
            return "Congelados";

        if (contiene(c, "despensa", "alimentacion", "arroz", "pasta", "legumbre", "aceite", "especia", "conserva"))
            return "Alimentación General";

        // =========================
        // 🧠 PASO 5: APOYO FINAL (Por Nombre)
        // =========================
        if (contiene(n, "manzana", "platano", "pera", "naranja", "uva", "limon", "melon", "pina", "tomate", "patata"))
            return "Frutas y Verduras";

        if (contiene(n, "leche", "yogur", "queso", "huevo"))
            return "Lácteos y Huevos";

        if (contiene(n, "cerveza", "vino", "refresco", "agua", "zumo"))
            return "Bebidas";

        if (contiene(n, "chocolate", "galleta", "azucar", "mermelada", "cereales", "miel", "cacao"))
            return "Dulces y Snacks";

        // =========================
        // ⚠️ FALLBACK
        // =========================
        if (catOriginal.isEmpty() ||
                catOriginal.startsWith("http") ||
                catOriginal.equalsIgnoreCase("frescos")) {
            return "General";
        }

        return catOriginal;
    }

    /**
     * Normaliza un texto: quita acentos y caracteres raros para facilitar la
     * búsqueda
     */
    private String normalizar(String texto) {
        if (texto == null)
            return "";
        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        return normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase();
    }

    /**
     * Comprueba si el texto contiene alguna de las palabras clave
     */
    private boolean contiene(String texto, String... palabras) {
        for (String p : palabras) {
            if (texto.contains(p))
                return true;
        }
        return false;
    }

}
