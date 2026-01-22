package com.example.demo.proyecto.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Usuario;

public interface repositoryUsuario extends JpaRepository<Usuario, Long> {
    @Query(value="SELECT * FROM usuarios WHERE usuario.rol = 'ADMIN'",nativeQuery = true)
    List<Usuario> listaAdmins();

}



