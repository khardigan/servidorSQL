package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.proyecto.dto.ProductoDTO;
import com.example.demo.proyecto.model.Producto;

public interface repositoryProducto extends JpaRepository<Producto, Long> {
       // Busca productos por nombre, ignorando mayúsculas y minúsculas.
       List<Producto> findByNombreContainingIgnoreCase(String nombre);

       @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.categoria != ''")
       List<String> findDistinctCategorias();

       @Query("SELECT DISTINCT p.supermercado FROM Producto p WHERE p.supermercado IS NOT NULL AND p.supermercado != ''")
       List<String> findDistinctSupermercados();

       @Query("SELECT new com.example.demo.proyecto.dto.ProductoDTO(" +
                     "p.id, p.nombre, p.descripcion, p.precio, p.supermercado, p.imagenUrl, p.categoria, " +
                     "AVG(COALESCE(c.puntuacion, 0)), COUNT(c)) " +
                     "FROM Producto p " +
                     "LEFT JOIN p.comentarios c " +
                     "GROUP BY p.id, p.nombre, p.descripcion, p.precio, p.supermercado, p.imagenUrl, p.categoria")
       List<ProductoDTO> findAllProductosConPuntuacion();

       // También para la búsqueda por nombre/descripción
       @Query("SELECT new com.example.demo.proyecto.dto.ProductoDTO(" +
                     "p.id, p.nombre, p.descripcion, p.precio, p.supermercado, p.imagenUrl, p.categoria, " +
                     "AVG(COALESCE(c.puntuacion, 0)), COUNT(c)) " +
                     "FROM Producto p " +
                     "LEFT JOIN p.comentarios c " +
                     "WHERE LOWER(p.nombre) LIKE LOWER(concat('%', :q, '%')) " +
                     "OR LOWER(p.descripcion) LIKE LOWER(concat('%', :q, '%')) " +
                     "GROUP BY p.id, p.nombre, p.descripcion, p.precio, p.supermercado, p.imagenUrl, p.categoria")
       List<ProductoDTO> buscarProductosConPuntuacion(@Param("q") String q);
}