package com.example.demo.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyecto.model.Usuario;

import java.util.Optional;

public interface repositoryUsuario extends JpaRepository<Usuario, Long> {
        /**
         * Busca un usuario por su nombre exacto, ignorando mayúsculas y minúsculas.
         * Utilizado para login o para verificar disponibilidad de nombres.
         */
        Usuario findByNombreIgnoreCase(String nombre);

        /**
         * Busca un usuario mediante el token único generado para verificar su cuenta
         * de correo electrónico tras el registro.
         */
        Usuario findByVerificationToken(String token);

        /**
         * Obtiene la lista de todos los usuarios que tienen privilegios de Administrador.
         */
        @Query(value = "SELECT * FROM usuario WHERE rol = 'ADMIN'", nativeQuery = true)
        List<Usuario> listaAdmins();

        /**
         * Busca un usuario exactamente por su dirección de correo electrónico.
         * Es el método principal para el login estándar.
         */
        Usuario findByEmail(String email);

        /**
         * Busca un usuario mediante el token único generado para el proceso de
         * recuperación de contraseña (olvido de contraseña).
         */
        Optional<Usuario> findByResetToken(String resetToken);

        /**
         * Busca un usuario comprobando que coincidan exactamente tanto su nombre como su email.
         * Muy útil para procesos de recuperación de cuentas para verificar identidad.
         */
        Optional<Usuario> findByNombreAndEmail(String nombre, String email);

        /**
         * Actualiza directamente en base de datos el token de reseteo y su fecha de expiración
         * para un usuario específico, mejorando el rendimiento al evitar cargar toda la entidad.
         */
        @org.springframework.data.jpa.repository.Modifying
        @Query("UPDATE Usuario u SET u.resetToken = :token, u.resetTokenExpiration = :expiration WHERE u.id = :id")
        void updateResetToken(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("token") String token,
                        @org.springframework.data.repository.query.Param("expiration") java.time.LocalDateTime expiration);

        /**
         * Actualiza la contraseña de un usuario y limpia los tokens de reseteo en una sola consulta.
         * Se ejecuta al finalizar con éxito el flujo de recuperación de contraseña.
         */
        @org.springframework.data.jpa.repository.Modifying
        @Query("UPDATE Usuario u SET u.contraseña = :password, u.resetToken = null, u.resetTokenExpiration = null WHERE u.id = :id")
        void updatePasswordAndClearToken(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("password") String password);
}
