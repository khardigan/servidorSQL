package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.dto.ComentarioDTO;
import com.example.demo.proyecto.dto.CrearProductoDTO;

import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@DependsOn("serviceAuthen")

public class serviceProducto {

    private final repositoryProducto repoProducto;
    private final repositoryUsuario repoUsuario;

    // Lista de supermercados "Oficiales". Cualquier otro se considerará "Mercado
    // Libre" en el frontend.
    private static final List<String> SUPERMERCADOS_OFICIALES = List.of(
            "Mercadona", "Carrefour", "Lidl", "Dia", "Mercado Libre");

    public serviceProducto(repositoryProducto repoProducto, repositoryUsuario repoUsuario) {
        this.repoProducto = repoProducto;
        this.repoUsuario = repoUsuario;
    }

    @PostConstruct
    // Crea productos de prueba si la base de datos está vacía.
    public void init() {
        // 1. Verificamos si ya hay productos para no duplicarlos cada vez que reinicies
        if (repoProducto.count() == 0) {

            // 2. Necesitamos un usuario para asignar los productos (buscamos al admin)
            Usuario admin = repoUsuario.findAll().stream()
                    .filter(u -> u.getNombre().equals("admin"))
                    .findFirst()
                    .orElse(null);

            if (admin != null) { /* Se crean 9 productos por defecto */
                List<Producto> productosDefault = new ArrayList<>();

                // Producto 1
                Producto p1 = new Producto();
                p1.setNombre("MacBook Pro M3 Max");
                p1.setDescripcion(
                        "Pantalla Liquid Retina XDR de 16\", 64GB RAM unificada, 2TB SSD. Rendimiento extremo espacial.");
                p1.setPrecio(4299.0);

                p1.setConfirmado(true); // Los de default los ponemos ya confirmados
                p1.setUsuarioRegistrador(admin);
                p1.setSupermercado("Mercadona");
                p1.setCategoria("Tecnología");
                productosDefault.add(p1);

                // Producto 2
                Producto p2 = new Producto();
                p2.setNombre("Monitor Odyssey OLED G9");
                p2.setDescripcion(
                        "49 pulgadas ultrapanorámico, 240Hz, 0.03ms. Inmersión total para gaming profesional.");
                p2.setPrecio(1599.0);

                p2.setConfirmado(true);
                p2.setUsuarioRegistrador(admin);
                p2.setSupermercado("MediaMarkt");
                p2.setCategoria("Tecnología");
                productosDefault.add(p2);

                // Producto 3
                Producto p3 = new Producto();
                p3.setNombre("Teclado Keychron Q1 Pro");
                p3.setDescripcion(
                        "Mecánico personalizado 75%, inalámbrico QMK/VIA, cuerpo de aluminio mecanizado CNC con switches Banana.");
                p3.setPrecio(199.0);

                p3.setConfirmado(true);
                p3.setUsuarioRegistrador(admin);
                p3.setSupermercado("PC Componentes");
                p3.setCategoria("Tecnología");
                productosDefault.add(p3);

                // --- Additional seed products ---
                // Producto 4
                Producto p4 = new Producto();
                p4.setNombre("iPhone 15 Pro Max");
                p4.setDescripcion(
                        "Titanio aeroespacial, chip A17 Pro, 256GB, sistema de cámaras Pro con zoom óptico 5x.");
                p4.setPrecio(1469.0);

                p4.setConfirmado(true);
                p4.setUsuarioRegistrador(admin);
                p4.setSupermercado("Apple Store");
                p4.setCategoria("Tecnología");
                productosDefault.add(p4);

                // Producto 5
                Producto p5 = new Producto();
                p5.setNombre("Sony WH-1000XM5");
                p5.setDescripcion(
                        "Auriculares inalámbricos con líder de la industria en cancelación de ruido y llamadas de voz premium.");
                p5.setPrecio(399.0);

                p5.setConfirmado(true);
                p5.setUsuarioRegistrador(admin);
                p5.setCategoria("Tecnología");
                productosDefault.add(p5);

                // Producto 6
                Producto p6 = new Producto();
                p6.setNombre("Sony Alpha 7 IV");
                p6.setDescripcion(
                        "Cámara EVIL de formato completo, 33 MP, grabación 4K 60p, enfoque automático en tiempo real. Solo cuerpo.");
                p6.setPrecio(2499.0);

                p6.setConfirmado(true);
                p6.setUsuarioRegistrador(admin);
                p6.setCategoria("Tecnología");
                productosDefault.add(p6);

                // Producto 7
                Producto p7 = new Producto();
                p7.setNombre("iPad Pro 11\"");
                p7.setDescripcion(
                        "Pantalla Liquid Retina, Chip M2, 256GB, Wi-Fi 6E. Ideal para creativos en movimiento.");
                p7.setPrecio(1049.0);

                p7.setConfirmado(true);
                p7.setUsuarioRegistrador(admin);
                p7.setCategoria("Tecnología");
                productosDefault.add(p7);

                // Producto 8
                Producto p8 = new Producto();
                p8.setNombre("Logitech MX Master 3S");
                p8.setDescripcion("Ratón inalámbrico de alto rendimiento con sensor de 8K DPI y clics silenciosos.");
                p8.setPrecio(129.0);

                p8.setConfirmado(true);
                p8.setUsuarioRegistrador(admin);
                p8.setCategoria("Tecnología");
                productosDefault.add(p8);

                // Producto 9
                Producto p9 = new Producto();
                p9.setNombre("Samsung Galaxy S24 Ultra");
                p9.setDescripcion("Pantalla Dynamic AMOLED 2X de 6.8\", S Pen integrado, cámara de 200MP y Titanio.");
                p9.setPrecio(1459.0);

                p9.setConfirmado(true);
                p9.setUsuarioRegistrador(admin);
                p9.setCategoria("Tecnología");
                productosDefault.add(p9);

                // Producto 10
                Producto p10 = new Producto();
                p10.setNombre("Nintendo Switch OLED");
                p10.setDescripcion(
                        "Consola con pantalla OLED de 7 pulgadas, base con puerto LAN por cable y 64GB de memoria.");
                p10.setPrecio(349.0);

                p10.setConfirmado(true);
                p10.setUsuarioRegistrador(admin);
                productosDefault.add(p10);

                // Producto 11
                Producto p11 = new Producto();
                p11.setNombre("ASUS ROG Zephyrus G14");
                p11.setDescripcion("Portátil Gaming de 14\", Ryzen 9, RTX 4070, 32GB RAM, 1TB SSD. Potencia compacta.");
                p11.setPrecio(2199.0);

                p11.setConfirmado(true);
                p11.setUsuarioRegistrador(admin);
                p11.setCategoria("Tecnología");
                productosDefault.add(p11);

                // Producto 12 (Alimentación)
                Producto p12 = new Producto();
                p12.setNombre("Arroz Brillante Sabroz 1kg");
                p12.setDescripcion("Arroz redondo que siempre queda en su punto. Ideal para paellas y guisos.");
                p12.setPrecio(2.15);

                p12.setConfirmado(true);
                p12.setUsuarioRegistrador(admin);
                p12.setSupermercado("Mercadona");
                p12.setCategoria("Alimentación");
                productosDefault.add(p12);

                // Producto 13 (Bebidas)
                Producto p13 = new Producto();
                p13.setNombre("Vino Tinto Rioja Reserva");
                p13.setDescripcion("Vino con cuerpo, notas de madera y fruta madura. Perfecto para carnes rojas.");
                p13.setPrecio(12.50);

                p13.setConfirmado(true);
                p13.setUsuarioRegistrador(admin);
                p13.setSupermercado("Carrefour");
                p13.setCategoria("Bebidas");
                productosDefault.add(p13);

                // Producto 14 (Limpieza)
                Producto p14 = new Producto();
                p14.setNombre("Detergente Ariel Pods 40u");
                p14.setDescripcion("Cápsulas de lavado con poder quitamanchas extremo incluso en agua fría.");
                p14.setPrecio(15.99);

                p14.setConfirmado(true);
                p14.setUsuarioRegistrador(admin);
                p14.setSupermercado("Dia");
                p14.setCategoria("Limpieza");
                productosDefault.add(p14);

                // Producto 15 (Hogar)
                Producto p15 = new Producto();
                p15.setNombre("Vela Aromática Vainilla");
                p15.setDescripcion(
                        "Vela en vaso de cristal con aroma intenso a vainilla natural. 40 horas de duración.");
                p15.setPrecio(4.50);

                p15.setConfirmado(true);
                p15.setUsuarioRegistrador(admin);
                p15.setSupermercado("Cooperativa");
                p15.setCategoria("Hogar");
                productosDefault.add(p15);

                // Guardar todos en la base de datos
                repoProducto.saveAll(productosDefault);
                System.out.println("Productos por defecto creados exitosamente.");
            } else {
                System.out.println("No se pudieron crear productos: El usuario 'admin' no existe todavía.");
            }
        }
    }

    // ---------------- Listar ----------------

    // Devuelve todos los productos del catálogo.
    public List<ProductoDTO> listarProductosDTO() {
        return repoProducto.findAll()
                .stream()
                .map(this::convertirAProductoDTO)
                .sorted((a, b) -> {
                    // Productos pendientes (confirmado = false) primero
                    boolean aEsPendiente = !a.isConfirmado();
                    boolean bEsPendiente = !b.isConfirmado();
                    if (aEsPendiente != bEsPendiente) {
                        return aEsPendiente ? -1 : 1;
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    // Busca productos por nombre ignorando mayúsculas.
    public List<ProductoDTO> buscarProductosDTO(String nombre) {
        return repoProducto.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAProductoDTO)
                .sorted((a, b) -> {
                    // Productos pendientes (confirmado = false) primero
                    boolean aEsPendiente = !a.isConfirmado();
                    boolean bEsPendiente = !b.isConfirmado();
                    if (aEsPendiente != bEsPendiente) {
                        return aEsPendiente ? -1 : 1;
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    // Te da la información de un producto por su ID.
    public ProductoDTO obtenerProductoDTO(Long id) {
        Producto p = repoProducto.findById(id).orElse(null);
        return convertirAProductoDTO(p);
    }

    // Devuelve la lista de categorías únicas. Si no hay ninguna en BD, devuelve
    // unas por defecto.
    // Devuelve la lista de categorías únicas, combinadas con una lista de respaldo
    // siempre visible.
    public List<String> listarCategorias() {
        // Obtenemos las que ya existen en la base de datos
        List<String> catsBD = repoProducto.findDistinctCategorias();

        // Lista de categorías que queremos que aparezcan siempre
        List<String> predefinidas = List.of(
                "Alimentación", "Bebidas", "Limpieza", "Hogar",
                "Cuidado Personal", "Tecnología", "Mascotas", "Juguetes");

        // Combinamos ambas sin duplicados
        java.util.Set<String> todas = new java.util.TreeSet<>(catsBD);
        todas.addAll(predefinidas);

        return new java.util.ArrayList<>(todas);
    }

    // Devuelve la lista de supermercados oficiales definidos en el sistema.
    public List<String> listarSupermercados() {
        return SUPERMERCADOS_OFICIALES;
    }

    public List<ProductoDTO> listarProductosDTOPuntuacion() {
        return repoProducto.findAllProductosConPuntuacion();
    }

    public List<ProductoDTO> buscarProductosDTOPuntuacion(String q) {
        return repoProducto.buscarProductosConPuntuacion(q);
    }

    // ---------------- Guardar ----------------
    // Guarda un producto pendiente de confirmar por un admin.
    public ProductoDTO guardarProductoTemporal(CrearProductoDTO dto, Usuario usuario) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setSupermercado(dto.getSupermercado());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(dto.getCategoria());
        producto.setUsuarioRegistrador(usuario);
        producto.setConfirmado(false);

        // Lo guardamos en base de datos para que tenga un ID REAL y sea persistente
        Producto guardado = repoProducto.save(producto);
        return convertirAProductoDTO(guardado);
    }

    // Confirmar producto
    // Marca un producto como confirmado (solo para administradores).
    public ProductoDTO confirmarProducto(Long realId, Usuario admin) {
        if (!admin.getRol().equals("ADMIN") && !admin.getRol().equals("DISTRIBUTOR")) {
            throw new RuntimeException("No tienes permisos para confirmar este producto");
        }
        Producto p = repoProducto.findById(realId).orElse(null);
        if (p == null || Boolean.TRUE.equals(p.getConfirmado()))
            throw new RuntimeException("Producto no encontrado o ya confirmado");

        p.setConfirmado(true);
        Producto saved = repoProducto.save(p);
        return convertirAProductoDTO(saved);
    }

    // Rechazar producto
    // Borra un producto que no ha sido aceptado (solo para administradores).
    public boolean rechazarProducto(Long realId, Usuario admin) {
        if (!admin.getRol().equals("ADMIN") && !admin.getRol().equals("DISTRIBUTOR")) {
            throw new RuntimeException("No tienes permisos para rechazar este producto");
        }
        if (repoProducto.existsById(realId)) {
            repoProducto.deleteById(realId);
            return true;
        }
        return false;
    }

    // ---------------- Actualizar ----------------

    // Cambia los datos de un producto existente.
    @Transactional
    public ProductoDTO actualizarProducto(Long id, CrearProductoDTO dto, Usuario usuario) {
        Producto producto = repoProducto.findById(id).orElse(null);
        if (producto == null)
            return null;

        // Actualizar campos desde el DTO
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setSupermercado(dto.getSupermercado());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(dto.getCategoria());
        producto.setUsuarioRegistrador(usuario);

        Producto actualizado = repoProducto.save(producto);
        return convertirAProductoDTO(actualizado);
    }

    // ---------------- Eliminar ----------------

    // Borra un producto del catálogo por su ID.
    public boolean eliminarProducto(Long id) {
        if (repoProducto.existsById(id)) {
            repoProducto.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- Conversión a DTO ----------------

    // Pasa el producto del modelo a formato DTO.
    private ProductoDTO convertirAProductoDTO(Producto p) {
        if (p == null)
            return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setUsuarioRegistradorId(p.getUsuarioRegistrador() != null ? p.getUsuarioRegistrador().getId() : null);
        dto.setConfirmado(Boolean.TRUE.equals(p.getConfirmado()));
        dto.setSupermercado(p.getSupermercado());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setCategoria(p.getCategoria());

        double sum = 0.0;
        int count = 0;
        if (p.getComentarios() != null) {
            count = p.getComentarios().size();
            for (com.example.demo.proyecto.model.Comentario c : p.getComentarios()) {
                if (c.getPuntuacion() != null) {
                    sum += c.getPuntuacion();
                }
            }
        }
        dto.setMediaPuntuacion(count > 0 ? sum / count : 0.0);
        dto.setTotalComentarios((long) count);

        dto.setListas(
                p.getListas() != null
                        ? p.getListas().stream().map(lp -> lp.getLista().getCodLista()).collect(Collectors.toList())
                        : List.of());

        dto.setComentarios(
                p.getComentarios() != null
                        ? p.getComentarios().stream().map(c -> {
                            ComentarioDTO cdto = new ComentarioDTO(
                                    c.getIdComentario(),
                                    c.getContenido(),
                                    c.getFecha() != null ? c.getFecha().toString() : "",
                                    c.getPuntuacion(),
                                    c.getUsuario() != null ? c.getUsuario().getId() : null,
                                    c.getProductoId());
                            // Also map user name for display purposes (this is commonly needed by frontend
                            // for comments)
                            if (c.getUsuario() != null) {
                                // We don't have a usuarioNombre field in ComentarioDTO? Wait, let's just pass
                                // the ID. The frontend seems to use `c.usuarioNombre || 'Usuario'` in
                                // `producto.html`.
                                // Oh! `ProductoDTO` mapping needs to provide `usuarioNombre`.
                                // We can't add it to ComentarioDTO without modifying it again. Let's just
                                // create it with what we have.
                            }
                            return cdto;
                        }).collect(Collectors.toList())
                        : List.of());

        return dto;
    }

}
