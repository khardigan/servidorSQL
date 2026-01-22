package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Producto;

public interface repositoryProducto extends JpaRepository<Producto, Long> {

}