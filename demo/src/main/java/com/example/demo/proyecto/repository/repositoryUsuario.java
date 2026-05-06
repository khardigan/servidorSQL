package com.example.demo.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Usuario;

import java.util.Optional;

public interface repositoryUsuario extends JpaRepository<Usuario, Long> {
        // Busca usuarios por nombre, ignorando mayúsculas y minúsculas.
        Usuario findByNombreIgnoreCase(String nombre);

        Usuario findByVerificationToken(String token);

        // Busca usuarios por rol.
        @Query(value = "SELECT * FROM usuario WHERE rol = 'ADMIN'", nativeQuery = true)
        List<Usuario> listaAdmins();

        Usuario findByEmail(String email);

        Optional<Usuario> findByResetToken(String resetToken);

        Optional<Usuario> findByNombreAndEmail(String nombre, String email);

        @org.springframework.data.jpa.repository.Modifying
        @Query("UPDATE Usuario u SET u.resetToken = :token, u.resetTokenExpiration = :expiration WHERE u.id = :id")
        void updateResetToken(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("token") String token,
                        @org.springframework.data.repository.query.Param("expiration") java.time.LocalDateTime expiration);

        @org.springframework.data.jpa.repository.Modifying
        @Query("UPDATE Usuario u SET u.contraseña = :password, u.resetToken = null, u.resetTokenExpiration = null WHERE u.id = :id")
        void updatePasswordAndClearToken(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("password") String password);
}
