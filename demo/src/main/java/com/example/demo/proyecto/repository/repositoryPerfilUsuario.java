package com.example.demo.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyecto.model.PerfilUsario;
import java.util.Optional;

public interface repositoryPerfilUsuario extends JpaRepository<PerfilUsario, Integer> {

    /**
     * Busca el perfil asociado a un ID de usuario específico.
     * @param usuarioId El ID del usuario dueño del perfil.
     * @return Optional con el perfil del usuario.
     */
    Optional<PerfilUsario> findByUsuarioId(Long usuarioId);

    /**
     * Verifica si ya existe un perfil con un nombre de perfil (nickname) específico.
     * Útil para asegurar que los nombres de perfil sean únicos en la comunidad.
     * @param nombrePerfil El nombre de perfil a verificar.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existsByNombrePerfil(String nombrePerfil);

    /**
     * Busca un perfil por su nombre público (nickname).
     * @param nombrePerfil El nombre de perfil a buscar.
     * @return Optional con el perfil si existe.
     */
    Optional<PerfilUsario> findByNombrePerfil(String nombrePerfil);
}
