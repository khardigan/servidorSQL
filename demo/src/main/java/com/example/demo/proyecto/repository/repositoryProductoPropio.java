package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.proyecto.model.ProductoPropio;

@Repository
public interface repositoryProductoPropio extends JpaRepository<ProductoPropio, Long> {
    
    List<ProductoPropio> findByNombreContainingIgnoreCase(String nombre);

    List<ProductoPropio> findByUsuarioId(Long usuarioId);
}
