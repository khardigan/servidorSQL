package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.CrearPerfilRequestDTO;
import com.example.demo.proyecto.dto.PerfilUsuarioDTO;
import com.example.demo.proyecto.model.PerfilUsario;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryPerfilUsuario;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.transaction.Transactional;

@Service
public class servicePerfilUsuario {

    private final repositoryPerfilUsuario repoPerfil;

    public servicePerfilUsuario(repositoryPerfilUsuario repoPerfil, repositoryUsuario repoUsuario) {
        this.repoPerfil = repoPerfil;
    }

    // ---------------- LISTAR ----------------
    // Devuelve la lista de todos los perfiles.
    public List<PerfilUsuarioDTO> listarPerfilesDTO() {
        List<PerfilUsario> perfiles = repoPerfil.findAll();
        List<PerfilUsuarioDTO> dtos = new ArrayList<>();
        for (PerfilUsario p : perfiles) {
            dtos.add(convertirADTO(p));
        }
        return dtos;
    }

    // ---------------- BUSCAR ----------------
    // Busca un perfil por su ID.
    public PerfilUsario buscarPerfilPorId(Integer id) {
        if (id == null)
            return null;
        return repoPerfil.findById(id).orElse(null);
    }

    // Te da los datos del perfil en formato DTO.
    public PerfilUsuarioDTO obtenerPerfilDTO(Integer id) {
        PerfilUsario p = buscarPerfilPorId(id);
        return convertirADTO(p);
    }

    public PerfilUsuarioDTO obtenerPerfilPorUsuarioId(Long usuarioId) {
        if (usuarioId == null)
            return null;
        PerfilUsario p = repoPerfil.findByUsuarioId(usuarioId).orElse(null);
        return convertirADTO(p);
    }

    // ---------------- GUARDAR ----------------
    @Transactional
    // Crea y guarda un nuevo perfil.
    public PerfilUsuarioDTO guardarPerfil(CrearPerfilRequestDTO dto) {
        PerfilUsario perfil = new PerfilUsario();
        perfil.setNombrePerfil(dto.getNombrePerfil());
        perfil.setEmail(dto.getEmail());
        perfil.setDescripcion(dto.getDescripcion());
        perfil.setSubtitulo(dto.getSubtitulo());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setEdad(dto.getEdad());
        perfil.setResidencia(dto.getResidencia());
        perfil.setTelefono(dto.getTelefono());

        PerfilUsario guardado = repoPerfil.save(perfil);
        return convertirADTO(guardado);
    }

    // ---------------- ACTUALIZAR ----------------
    @Transactional
    // Actualiza los datos de un perfil.
    public PerfilUsuarioDTO actualizarPerfil(Integer id, CrearPerfilRequestDTO dtoRequest) {
        PerfilUsario perfil = repoPerfil.findById(id).orElse(null);
        if (perfil == null)
            return null;

        if (dtoRequest.getDescripcion() != null)
            perfil.setDescripcion(dtoRequest.getDescripcion());
        if (dtoRequest.getNombrePerfil() != null)
            perfil.setNombrePerfil(dtoRequest.getNombrePerfil());
        if (dtoRequest.getSubtitulo() != null)
            perfil.setSubtitulo(dtoRequest.getSubtitulo());
        if (dtoRequest.getFechaNacimiento() != null)
            perfil.setFechaNacimiento(dtoRequest.getFechaNacimiento());
        if (dtoRequest.getEdad() != null)
            perfil.setEdad(dtoRequest.getEdad());
        if (dtoRequest.getResidencia() != null)
            perfil.setResidencia(dtoRequest.getResidencia());
        if (dtoRequest.getEmail() != null)
            perfil.setEmail(dtoRequest.getEmail());
        if (dtoRequest.getTelefono() != null)
            perfil.setTelefono(dtoRequest.getTelefono());

        PerfilUsario actualizado = repoPerfil.save(perfil);
        return convertirADTO(actualizado);
    }

    // ---------------- ELIMINAR ----------------
    @Transactional
    // Elimina un perfil por su ID.
    public boolean eliminarPerfil(Integer id) {
        if (repoPerfil.existsById(id)) {
            repoPerfil.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- USUARIO DEL PERFIL ----------------
    // Te da el usuario que tiene ese perfil.
    public Usuario obtenerUsuarioDelPerfil(Integer id) {
        if (id == null)
            return null;
        PerfilUsario perfil = repoPerfil.findById(id).orElse(null);
        if (perfil == null)
            return null;
        return perfil.getUsuario(); // puede ser null
    }

    // ---------------- CONVERSIÓN DTO ----------------
    // Pasa un perfil del modelo a formato DTO.
    private PerfilUsuarioDTO convertirADTO(PerfilUsario p) {
        if (p == null)
            return null;
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();
        dto.setUsuarioId(p.getUsuario() != null ? p.getUsuario().getId() : null);
        dto.setNombrePerfil(p.getNombrePerfil());
        dto.setDescripcion(p.getDescripcion());
        dto.setSubtitulo(p.getSubtitulo());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setEdad(p.getEdad());
        dto.setResidencia(p.getResidencia());
        // Fallback al email del usuario si el del perfil está vacío
        dto.setEmail(p.getEmail() != null ? p.getEmail()
                : (p.getUsuario() != null ? p.getUsuario().getEmail() : null));
        dto.setTelefono(p.getTelefono());
        dto.setIdPerfil(p.getId()); // añadir el id Integer al DTO
        return dto;
    }
}
