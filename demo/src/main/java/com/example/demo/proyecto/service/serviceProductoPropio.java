package com.example.demo.proyecto.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.example.demo.proyecto.repository.repositoryProductoPropio;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.dto.CrearProductoPropioDTO;
import com.example.demo.proyecto.dto.ProductoPropioDTO;
import com.example.demo.proyecto.model.ProductoPropio;
import com.example.demo.proyecto.model.Usuario;

@Service
public class serviceProductoPropio {

    private final repositoryProductoPropio repoProductoPropio;
    private final repositoryUsuario repoUsuario;
    private final com.example.demo.proyecto.repository.repositoryLista repoLista;

    public serviceProductoPropio(repositoryProductoPropio repoProductoPropio, repositoryUsuario repoUsuario,
            com.example.demo.proyecto.repository.repositoryLista repoLista) {
        this.repoProductoPropio = repoProductoPropio;
        this.repoUsuario = repoUsuario;
        this.repoLista = repoLista;
    }

    // Te da la lista de productos propios de un usuario.
    public List<ProductoPropioDTO> obtenerLista(Long usuarioId) {
        List<ProductoPropio> productos = repoProductoPropio.findByUsuarioId(usuarioId);
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Crea un producto nuevo dentro de una lista.
    public ProductoPropioDTO crearItem(Long usuarioId, CrearProductoPropioDTO dto) {
        Usuario usuario = repoUsuario.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoPropio producto = new ProductoPropio();
        producto.setUsuario(usuario);
        producto.setNombre(dto.getNombre());
        producto.setPrecioObjetivo(dto.getPrecioObjetivo());
        producto.setNotas(dto.getNotas());
        producto.setSupermercado(dto.getSupermercado());
        producto.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 1);
        producto.setComprado(dto.getComprado() != null ? dto.getComprado() : false);

        // Asociar con lista si se proporciona el ID
        if (dto.getListaId() != null) {
            producto.setLista(repoLista.findById(dto.getListaId()).orElse(null));
        }

        ProductoPropio guardado = repoProductoPropio.save(producto);
        return convertirADTO(guardado);
    }

    @SuppressWarnings("null")
    // Actualiza los datos de un producto propio.
    public ProductoPropioDTO actualizarItem(Long id, CrearProductoPropioDTO dto) {
        ProductoPropio producto = repoProductoPropio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setPrecioObjetivo(dto.getPrecioObjetivo());
        producto.setNotas(dto.getNotas());
        producto.setSupermercado(dto.getSupermercado());

        // Actualizar asociación con lista: si viene null, se desvincula.
        if (dto.getListaId() != null) {
            producto.setLista(repoLista.findById(dto.getListaId()).orElse(null));
        } else {
            producto.setLista(null);
        }

        if (dto.getCantidad() != null) {
            producto.setCantidad(dto.getCantidad());
        }

        if (dto.getComprado() != null) {
            producto.setComprado(dto.getComprado());
        }

        ProductoPropio actualizado = repoProductoPropio.save(producto);
        return convertirADTO(actualizado);
    }

    // Elimina un producto propio del sistema.
    public void eliminarItem(Long id) {
        repoProductoPropio.deleteById(id);
    }

    // Pasa el producto del modelo a formato DTO.
    private ProductoPropioDTO convertirADTO(ProductoPropio producto) {
        ProductoPropioDTO dto = new ProductoPropioDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecioObjetivo(producto.getPrecioObjetivo());
        dto.setNotas(producto.getNotas());
        dto.setSupermercado(producto.getSupermercado());
        dto.setCantidad(producto.getCantidad());
        dto.setComprado(producto.getComprado());
        dto.setUsuarioId(producto.getUsuario().getId()); // Quien lo creó
        dto.setListaId(producto.getLista() != null ? producto.getLista().getCodLista() : null);
        dto.setCreatedAt(producto.getCreatedAt());
        return dto;
    }
}
