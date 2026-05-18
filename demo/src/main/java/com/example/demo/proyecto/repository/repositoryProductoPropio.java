package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.proyecto.model.ProductoPropio;

@Repository
public interface repositoryProductoPropio extends JpaRepository<ProductoPropio, Long> {
    /**
     * Busca productos propios (creados manualmente por los usuarios) cuyo nombre
     * contenga la cadena proporcionada, ignorando mayúsculas y minúsculas.
     * @param nombre Texto a buscar.
     * @return Lista de productos propios coincidentes.
     */
    List<ProductoPropio> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene todos los productos personalizados creados por un usuario específico.
     * @param usuarioId El ID del usuario.
     * @return Lista de productos propios de ese usuario.
     */
    List<ProductoPropio> findByUsuarioId(Long usuarioId);
}
