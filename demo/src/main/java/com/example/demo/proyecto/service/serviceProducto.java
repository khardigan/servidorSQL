package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;

@Service
public class serviceProducto {

    private final repositoryProducto repoProducto;
    private final repositoryUsuario repoUsuario;

    public serviceProducto(repositoryProducto repoProducto, repositoryUsuario repoUsuario) {
        this.repoProducto = repoProducto;
        this.repoUsuario = repoUsuario;
    }

    public List<ProductoDTO> listarProductosDTO() {
        return repoProducto.findAll().stream().map(this::convertirAProductoDTO).collect(Collectors.toList());
    }

    public ProductoDTO obtenerProductoDTO(Long id) {
        Producto p = repoProducto.findById(id).orElse(null);
        return convertirAProductoDTO(p);
    }

   @Transactional
    public ProductoDTO guardarProducto(ProductoDTO dto) {
        // Validación de duplicados
        if (dto.getNombre() != null) {
            boolean existe = repoProducto.findAll().stream()
                    .anyMatch(p -> p.getNombre() != null && p.getNombre().equals(dto.getNombre()));
            if (existe) {
                throw new com.example.demo.proyecto.exception.RecursoDuplicadoException("Producto ya existe en la base de datos");
            }
        }
        // Convertir DTO a entidad
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        // Asociar usuario si existe
        if (dto.getUsuarioRegistradorId() != null) {
            Usuario u = repoUsuario.findById(dto.getUsuarioRegistradorId()).orElse(null);
            if (u != null) {
                producto.setUsuarioRegistrador(u);
            }
        }
        // Guardar en la base de datos
        Producto saved = repoProducto.save(producto);
        // Actualizar la lista de productos del usuario
        if (saved.getUsuarioRegistrador() != null) {
            Usuario u = saved.getUsuarioRegistrador();
            List<Producto> lista = u.getListaProductosSubidos();
            if (lista == null) lista = new ArrayList<>();
            lista.add(saved);
            u.setListaProductosSubidos(lista);
            repoUsuario.save(u);
        }

        // Convertir la entidad guardada a DTO y devolver
        return convertirAProductoDTO(saved);
    }

   @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO dto) {
        Producto producto = repoProducto.findById(id).orElse(null);
        if (producto == null) return null;

        // Actualizar campos si vienen en el DTO
        if (dto.getNombre() != null) producto.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) producto.setDescripcion(dto.getDescripcion());
        if (dto.getPrecio() != null) producto.setPrecio(dto.getPrecio());
        if (dto.getCantidad() != 0) producto.setCantidad(dto.getCantidad());

        // Actualizar usuario registrador
        if (dto.getUsuarioRegistradorId() != null) {
            Usuario u = repoUsuario.findById(dto.getUsuarioRegistradorId()).orElse(null);
            if (u != null) producto.setUsuarioRegistrador(u);
        }

        Producto actualizado = repoProducto.save(producto);
        return convertirAProductoDTO(actualizado);
    }


    public boolean eliminarProducto(Long id) {
        if (repoProducto.existsById(id)) {
            repoProducto.deleteById(id);
            return true;
        }
        return false;
    }

    // ----------------- Conversión a DTO -----------------

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
