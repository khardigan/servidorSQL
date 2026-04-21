package com.example.demo.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Lista;

public interface repositoryLista extends JpaRepository<Lista, Long> {
    Optional<Lista> findByCodigo(String codigo);
}

