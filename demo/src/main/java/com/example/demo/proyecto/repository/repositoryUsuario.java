package com.example.demo.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Usuario;

public interface repositoryUsuario extends JpaRepository<Usuario, Long> {
    // Busca usuarios por nombre, ignorando mayúsculas y minúsculas.
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Busca usuarios por rol.
    @Query(value = "SELECT * FROM usuario WHERE rol = 'ADMIN'", nativeQuery = true)
    List<Usuario> listaAdmins();

}
