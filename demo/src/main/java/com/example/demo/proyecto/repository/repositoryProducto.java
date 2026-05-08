package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Producto;

public interface repositoryProducto extends JpaRepository<Producto, Long> {
       // Busca productos por nombre, ignorando mayúsculas y minúsculas.
       List<Producto> findByNombreContainingIgnoreCase(String nombre);

       void deleteByIdGreaterThan(Long id);

       @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.categoria != ''")
       List<String> findDistinctCategorias();

       @Query("SELECT DISTINCT p.supermercado FROM Producto p WHERE p.supermercado IS NOT NULL AND p.supermercado != ''")
       List<String> findDistinctSupermercados();

       @org.springframework.data.jpa.repository.Modifying
       @org.springframework.transaction.annotation.Transactional
       @Query(value = "DROP TABLE IF EXISTS producto_categorias CASCADE", nativeQuery = true)
       void limpiarTablasHuerfanas();

       @org.springframework.transaction.annotation.Transactional
       void deleteBySupermercado(String supermercado);

       @Query("SELECT p.nombre, p.supermercado FROM Producto p")
       List<Object[]> findAllNombresYSupermercados();
}