package com.example.demo.proyecto.service;

import com.example.demo.proyecto.dto.ComentarioDTO;
import com.example.demo.proyecto.dto.CrearComentarioDTO;
import com.example.demo.proyecto.model.Comentario;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryComentario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class serviceComentario {

    private final repositoryComentario repoComentario;
    private final repositoryUsuario repoUsuario;
    private final repositoryProducto repoProducto;

    public serviceComentario(repositoryComentario repoComentario, repositoryUsuario repoUsuario,
            repositoryProducto repoProducto) {
        this.repoComentario = repoComentario;
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
    }

    @Transactional
    public boolean agregarComentario(CrearComentarioDTO dto) {
        if (dto.getIdUsuario() == null || dto.getIdProducto() == null) {
            return false;
        }
        Usuario usuario = repoUsuario.findById(dto.getIdUsuario()).orElse(null);
        if (usuario == null)
            return false;

        Producto producto = repoProducto.findById(dto.getIdProducto()).orElse(null);
        if (producto == null)
            return false;

        // Validar que el usuario no haya comentado ya en este producto
        boolean yaComento = repoComentario.findAll().stream()
                .anyMatch(c -> c.getUsuario() != null &&
                        c.getUsuario().getId().equals(usuario.getId()) &&
                        c.getProductoId() != null &&
                        c.getProductoId().equals(producto.getId()));
        if (yaComento) {
            return false;
        }

        // Crear entidad
        Comentario c = new Comentario(
                dto.getContenido(),
                java.sql.Date.valueOf(LocalDate.now()),
                dto.getPuntuacion(),
                usuario);

        c.setProducto(producto);

        // Guardar comentario directamente
        repoComentario.save(c);
        return true;
    }

    public List<ComentarioDTO> obtenerTodosComentarios() {
        return repoComentario.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ComentarioDTO> obtenerMisComentarios(Long idUsuario) {
        if (idUsuario == null)
            return new ArrayList<>();
        Usuario u = repoUsuario.findById(idUsuario).orElse(null);
        if (u == null)
            return new ArrayList<>();

        return u.getComentarios().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ComentarioDTO> obtenerComentariosPorProducto(Long idProducto) {
        return repoComentario.findAll().stream()
                .filter(c -> c.getProducto() != null && c.getProducto().getId().equals(idProducto))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean actualizarComentario(Long idComentario, String contenido, Double puntuacion) {
        Comentario comentario = repoComentario.findById(idComentario).orElse(null);
        if (comentario == null)
            return false;

        comentario.setContenido(contenido);
        comentario.setPuntuacion(puntuacion);
        comentario.setFecha(java.sql.Date.valueOf(LocalDate.now()));
        repoComentario.save(comentario);
        return true;
    }

    @Transactional
    public boolean eliminarComentario(Long idComentario) {
        if (!repoComentario.existsById(idComentario))
            return false;
        repoComentario.deleteById(idComentario);
        return true;
    }

    // Método helper para conversión
    private ComentarioDTO convertirADTO(Comentario c) {
        ComentarioDTO dto = new ComentarioDTO(
                c.getIdComentario(),
                c.getContenido(),
                c.getFecha() != null ? c.getFecha().toString() : "",
                c.getPuntuacion(),
                c.getUsuario() != null ? c.getUsuario().getId() : null,
                c.getProductoId());

        if (c.getUsuario() != null) {
            dto.setNombreUsuario(c.getUsuario().getNombre());
            if (c.getUsuario().getPerfilUsuario() != null) {
                dto.setNickAutor(c.getUsuario().getPerfilUsuario().getNombrePerfil());
                dto.setImagenAutorUrl(c.getUsuario().getPerfilUsuario().getImagenUrl());
            } else {
                // Fallback: Si no hay perfil, usamos el nombre de usuario
                dto.setNickAutor(c.getUsuario().getNombre());
            }
        }
        return dto;
    }

}