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

    public serviceProductoPropio(repositoryProductoPropio repoProductoPropio, repositoryUsuario repoUsuario) {
        this.repoProductoPropio = repoProductoPropio;
        this.repoUsuario = repoUsuario;
    }

    public List<ProductoPropioDTO> obtenerLista(Long usuarioId) {
        List<ProductoPropio> productos = repoProductoPropio.findByUsuarioId(usuarioId);
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProductoPropioDTO crearItem(Long usuarioId, CrearProductoPropioDTO dto) {
        Usuario usuario = repoUsuario.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoPropio producto = new ProductoPropio();
        producto.setUsuario(usuario);
        producto.setNombre(dto.getNombre());
        producto.setPrecioObjetivo(dto.getPrecioObjetivo());
        producto.setNotas(dto.getNotas());

        ProductoPropio guardado = repoProductoPropio.save(producto);
        return convertirADTO(guardado);
    }

    public ProductoPropioDTO actualizarItem(Long id, CrearProductoPropioDTO dto) {
        ProductoPropio producto = repoProductoPropio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setPrecioObjetivo(dto.getPrecioObjetivo());
        producto.setNotas(dto.getNotas());

        ProductoPropio actualizado = repoProductoPropio.save(producto);
        return convertirADTO(actualizado);
    }

    public void eliminarItem(Long id) {
        repoProductoPropio.deleteById(id);
    }

    private ProductoPropioDTO convertirADTO(ProductoPropio producto) {
        ProductoPropioDTO dto = new ProductoPropioDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecioObjetivo(producto.getPrecioObjetivo());
        dto.setNotas(producto.getNotas());
        dto.setCreatedAt(producto.getCreatedAt());
        return dto;
    }
}
