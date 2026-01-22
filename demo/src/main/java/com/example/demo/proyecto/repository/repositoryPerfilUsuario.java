package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.PerfilUsario;

public interface repositoryPerfilUsuario extends JpaRepository<PerfilUsario, Integer> {
    
}

