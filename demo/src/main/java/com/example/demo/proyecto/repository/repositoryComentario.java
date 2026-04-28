package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.proyecto.model.Comentario;

@Repository
public interface repositoryComentario extends JpaRepository<Comentario, Long> {
}
