package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.dto.ProductoRequestDTO;
import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.transaction.Transactional;

@Service
public class serviceProducto {

    private final repositoryProducto repoProducto;
    private final repositoryUsuario repoUsuario;
    private final Map<Long, Producto> productosPendientes = new HashMap<>();
    private long nextTempId = 1L;


    public serviceProducto(repositoryProducto repoProducto, repositoryUsuario repoUsuario) {
        this.repoProducto = repoProducto;
        this.repoUsuario = repoUsuario;
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
    public ProductoDTO guardarProductoTemporal(ProductoRequestDTO dto, Usuario usuario) {
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
    public ProductoDTO actualizarProducto(Long id, ProductoRequestDTO dto, Usuario usuario) {
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
