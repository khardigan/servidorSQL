package com.example.demo.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Usuario;
import java.util.List;

public interface repositoryLista extends JpaRepository<Lista, Long> {
    /**
     * Busca una lista por su código único de compartición (ej. ABC-123).
     * @param codigo El código único generado para la lista.
     * @return Un Optional que contiene la lista si se encuentra.
     */
    Optional<Lista> findByCodigo(String codigo);

    /**
     * Encuentra todas las listas que pertenecen a un usuario específico.
     * @param usuario El usuario dueño de las listas.
     * @return Una lista de entidades Lista creadas por el usuario.
     */
    List<Lista> findByUsuarioDueno(Usuario usuario);
}
