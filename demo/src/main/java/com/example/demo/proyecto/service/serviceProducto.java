package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.ProductoDTO;
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
                p1.setCantidad(10);
                p1.setConfirmado(true); // Los de default los ponemos ya confirmados
                p1.setUsuarioRegistrador(admin);
                p1.setSupermercado("Mercadona");
                productosDefault.add(p1);

                // Producto 2
                Producto p2 = new Producto();
                p2.setNombre("Monitor Odyssey OLED G9");
                p2.setDescripcion(
                        "49 pulgadas ultrapanorámico, 240Hz, 0.03ms. Inmersión total para gaming profesional.");
                p2.setPrecio(1599.0);
                p2.setCantidad(15);
                p2.setConfirmado(true);
                p2.setUsuarioRegistrador(admin);
                p2.setSupermercado("MediaMarkt");
                productosDefault.add(p2);

                // Producto 3
                Producto p3 = new Producto();
                p3.setNombre("Teclado Keychron Q1 Pro");
                p3.setDescripcion(
                        "Mecánico personalizado 75%, inalámbrico QMK/VIA, cuerpo de aluminio mecanizado CNC con switches Banana.");
                p3.setPrecio(199.0);
                p3.setCantidad(50);
                p3.setConfirmado(true);
                p3.setUsuarioRegistrador(admin);
                p3.setSupermercado("PC Componentes");
                productosDefault.add(p3);

                // --- Additional seed products ---
                // Producto 4
                Producto p4 = new Producto();
                p4.setNombre("iPhone 15 Pro Max");
                p4.setDescripcion(
                        "Titanio aeroespacial, chip A17 Pro, 256GB, sistema de cámaras Pro con zoom óptico 5x.");
                p4.setPrecio(1469.0);
                p4.setCantidad(30);
                p4.setConfirmado(true);
                p4.setUsuarioRegistrador(admin);
                p4.setSupermercado("Apple Store");
                productosDefault.add(p4);

                // Producto 5
                Producto p5 = new Producto();
                p5.setNombre("Sony WH-1000XM5");
                p5.setDescripcion(
                        "Auriculares inalámbricos con líder de la industria en cancelación de ruido y llamadas de voz premium.");
                p5.setPrecio(399.0);
                p5.setCantidad(40);
                p5.setConfirmado(true);
                p5.setUsuarioRegistrador(admin);
                productosDefault.add(p5);

                // Producto 6
                Producto p6 = new Producto();
                p6.setNombre("Sony Alpha 7 IV");
                p6.setDescripcion(
                        "Cámara EVIL de formato completo, 33 MP, grabación 4K 60p, enfoque automático en tiempo real. Solo cuerpo.");
                p6.setPrecio(2499.0);
                p6.setCantidad(15);
                p6.setConfirmado(true);
                p6.setUsuarioRegistrador(admin);
                productosDefault.add(p6);

                // Producto 7
                Producto p7 = new Producto();
                p7.setNombre("iPad Pro 11\"");
                p7.setDescripcion(
                        "Pantalla Liquid Retina, Chip M2, 256GB, Wi-Fi 6E. Ideal para creativos en movimiento.");
                p7.setPrecio(1049.0);
                p7.setCantidad(25);
                p7.setConfirmado(true);
                p7.setUsuarioRegistrador(admin);
                productosDefault.add(p7);

                // Producto 8
                Producto p8 = new Producto();
                p8.setNombre("Logitech MX Master 3S");
                p8.setDescripcion("Ratón inalámbrico de alto rendimiento con sensor de 8K DPI y clics silenciosos.");
                p8.setPrecio(129.0);
                p8.setCantidad(100);
                p8.setConfirmado(true);
                p8.setUsuarioRegistrador(admin);
                productosDefault.add(p8);

                // Producto 9
                Producto p9 = new Producto();
                p9.setNombre("Samsung Galaxy S24 Ultra");
                p9.setDescripcion("Pantalla Dynamic AMOLED 2X de 6.8\", S Pen integrado, cámara de 200MP y Titanio.");
                p9.setPrecio(1459.0);
                p9.setCantidad(20);
                p9.setConfirmado(true);
                p9.setUsuarioRegistrador(admin);
                productosDefault.add(p9);

                // Producto 10
                Producto p10 = new Producto();
                p10.setNombre("Nintendo Switch OLED");
                p10.setDescripcion(
                        "Consola con pantalla OLED de 7 pulgadas, base con puerto LAN por cable y 64GB de memoria.");
                p10.setPrecio(349.0);
                p10.setCantidad(60);
                p10.setConfirmado(true);
                p10.setUsuarioRegistrador(admin);
                productosDefault.add(p10);

                // Producto 11
                Producto p11 = new Producto();
                p11.setNombre("ASUS ROG Zephyrus G14");
                p11.setDescripcion("Portátil Gaming de 14\", Ryzen 9, RTX 4070, 32GB RAM, 1TB SSD. Potencia compacta.");
                p11.setPrecio(2199.0);
                p11.setCantidad(8);
                p11.setConfirmado(true);
                p11.setUsuarioRegistrador(admin);
                productosDefault.add(p11);

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

    // ---------------- Guardar ----------------
    // Guarda un producto pendiente de confirmar por un admin.
    public ProductoDTO guardarProductoTemporal(CrearProductoDTO dto, Usuario usuario) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setSupermercado(dto.getSupermercado());
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
        producto.setCantidad(dto.getCantidad());
        producto.setSupermercado(dto.getSupermercado());
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
        dto.setCantidad(p.getCantidad());
        dto.setUsuarioRegistradorId(p.getUsuarioRegistrador() != null ? p.getUsuarioRegistrador().getId() : null);
        dto.setConfirmado(Boolean.TRUE.equals(p.getConfirmado()));
        dto.setSupermercado(p.getSupermercado());

        dto.setListas(
                p.getListas() != null
                        ? p.getListas().stream().map(lp -> lp.getLista().getCodLista()).collect(Collectors.toList())
                        : List.of());

        return dto;
    }

}
