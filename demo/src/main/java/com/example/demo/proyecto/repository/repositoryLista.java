package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Lista;

public interface repositoryLista extends JpaRepository<Lista, Long> {
    
}

