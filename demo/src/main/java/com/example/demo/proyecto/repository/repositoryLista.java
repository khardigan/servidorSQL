package com.example.demo.proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Usuario;
import java.util.List;

public interface repositoryLista extends JpaRepository<Lista, Long> {
    Optional<Lista> findByCodigo(String codigo);

    // metodo para encontrar todas las listas de un usuario
    List<Lista> findByUsuarioDueno(Usuario usuario);
}
