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
import com.example.demo.proyecto.model.Lista;
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
    private final Map<Long, Producto> productosPendientes = new HashMap<>();
    private long nextTempId = 1L;


    public serviceProducto(repositoryProducto repoProducto, repositoryUsuario repoUsuario) {
        this.repoProducto = repoProducto;
        this.repoUsuario = repoUsuario;
    }


    @PostConstruct
    public void init() {
        // 1. Verificamos si ya hay productos para no duplicarlos cada vez que reinicies
        if (repoProducto.count() == 0) {
            
            // 2. Necesitamos un usuario para asignar los productos (buscamos al admin)
            Usuario admin = repoUsuario.findAll().stream()
                    .filter(u -> u.getNombre().equals("admin"))
                    .findFirst()
                    .orElse(null);

            if (admin != null) {
                List<Producto> productosDefault = new ArrayList<>();

                // Producto 1
                Producto p1 = new Producto();
                p1.setNombre("Laptop Gaming");
                p1.setDescripcion("Procesador i9, 32GB RAM, RTX 4080");
                p1.setPrecio(2500.0);
                p1.setCantidad(10);
                p1.setConfirmado(true); // Los de default los ponemos ya confirmados
                p1.setUsuarioRegistrador(admin);
                productosDefault.add(p1);

                // Producto 2
                Producto p2 = new Producto();
                p2.setNombre("Monitor 4K");
                p2.setDescripcion("32 pulgadas, 144Hz, HDR10");
                p2.setPrecio(600.0);
                p2.setCantidad(15);
                p2.setConfirmado(true);
                p2.setUsuarioRegistrador(admin);
                productosDefault.add(p2);

                // Producto 3
                Producto p3 = new Producto();
                p3.setNombre("Teclado Mecánico");
                p3.setDescripcion("RGB, Switches Blue, Layout Español");
                p3.setPrecio(120.0);
                p3.setCantidad(50);
                p3.setConfirmado(true);
                p3.setUsuarioRegistrador(admin);
                productosDefault.add(p3);

                // Guardar todos en la base de datos
                repoProducto.saveAll(productosDefault);
                System.out.println("Productos por defecto creados exitosamente.");
            } else {
                System.out.println("No se pudieron crear productos: El usuario 'admin' no existe todavía.");
            }
        }
    }



    // ---------------- Listar ----------------

    public List<ProductoDTO> listarProductosDTO() {
        return repoProducto.findAll()
                .stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO obtenerProductoDTO(Long id) {
        Producto p = repoProducto.findById(id).orElse(null);
        return convertirAProductoDTO(p);
    }

    // ---------------- Guardar ----------------
    public ProductoDTO guardarProductoTemporal(CrearProductoDTO dto, Usuario usuario) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setUsuarioRegistrador(usuario);
        producto.setConfirmado(false);

        long tempId = nextTempId++;
        productosPendientes.put(tempId, producto);

        ProductoDTO dtoResp = new ProductoDTO();
        dtoResp.setId(tempId);
        dtoResp.setNombre(producto.getNombre());
        dtoResp.setDescripcion(producto.getDescripcion());
        dtoResp.setPrecio(producto.getPrecio());
        dtoResp.setCantidad(producto.getCantidad());
        dtoResp.setUsuarioRegistradorId(usuario.getId());
        dtoResp.setConfirmado(false);
        return dtoResp;
    }

    // Confirmar producto
    public ProductoDTO confirmarProducto(Long tempId, Usuario admin) {
        if (!admin.getRol().equals("ADMIN") && !admin.getRol().equals("DISTRIBUTOR")) {
            throw new RuntimeException("No tienes permisos para confirmar este producto");
        }
        Producto p = productosPendientes.remove(tempId);
        if (p == null) throw new RuntimeException("Producto no encontrado o ya confirmado");

        p.setConfirmado(true);
        Producto saved = repoProducto.save(p);
        return convertirAProductoDTO(saved);
    }

    // Rechazar producto
    public boolean rechazarProducto(Long tempId, Usuario admin) {
        if (!admin.getRol().equals("ADMIN") && !admin.getRol().equals("DISTRIBUTOR")) {
            throw new RuntimeException("No tienes permisos para rechazar este producto");
        }
        return productosPendientes.remove(tempId) != null;
    }



    // ---------------- Actualizar ----------------

    @Transactional
    public ProductoDTO actualizarProducto(Long id, CrearProductoDTO dto, Usuario usuario) {
        Producto producto = repoProducto.findById(id).orElse(null);
        if (producto == null) return null;

        // Actualizar campos desde el DTO
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setUsuarioRegistrador(usuario);

        Producto actualizado = repoProducto.save(producto);
        return convertirAProductoDTO(actualizado);
    }

    // ---------------- Eliminar ----------------

    public boolean eliminarProducto(Long id) {
        if (repoProducto.existsById(id)) {
            repoProducto.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- Conversión a DTO ----------------

    private ProductoDTO convertirAProductoDTO(Producto p) {
        if (p == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setCantidad(p.getCantidad());
        dto.setUsuarioRegistradorId(p.getUsuarioRegistrador() != null ? p.getUsuarioRegistrador().getId() : null);

        dto.setListas(
                p.getListas() != null
                        ? p.getListas().stream().map(Lista::getCodLista).collect(Collectors.toList())
                        : List.of()
        );

        return dto;
    }





}
