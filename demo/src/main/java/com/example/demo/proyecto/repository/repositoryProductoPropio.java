package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.proyecto.model.ProductoPropio;

@Repository
public interface repositoryProductoPropio extends JpaRepository<ProductoPropio, Long> {
    // Busca productos por nombre, ignorando mayúsculas y minúsculas.
    List<ProductoPropio> findByNombreContainingIgnoreCase(String nombre);

    // Busca productos por usuario.
    List<ProductoPropio> findByUsuarioId(Long usuarioId);
}
