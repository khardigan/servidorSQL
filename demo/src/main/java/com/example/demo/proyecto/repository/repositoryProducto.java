package com.example.demo.proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Producto;

public interface repositoryProducto extends JpaRepository<Producto, Long> {
    // Busca productos por nombre, ignorando mayúsculas y minúsculas.
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}