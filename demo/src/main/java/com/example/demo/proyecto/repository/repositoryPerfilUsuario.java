package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.PerfilUsario;
import java.util.Optional;

public interface repositoryPerfilUsuario extends JpaRepository<PerfilUsario, Integer> {

    Optional<PerfilUsario> findByUsuarioId(Long usuarioId);

    boolean existsByNombrePerfil(String nombrePerfil);

    Optional<PerfilUsario> findByNombrePerfil(String nombrePerfil);
}
