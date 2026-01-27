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
    public List<PerfilUsuarioDTO> listarPerfilesDTO() {
        List<PerfilUsario> perfiles = repoPerfil.findAll();
        List<PerfilUsuarioDTO> dtos = new ArrayList<>();
        for (PerfilUsario p : perfiles) {
            dtos.add(convertirADTO(p));
        }
        return dtos;
    }

    // ---------------- BUSCAR ----------------
    public PerfilUsario buscarPerfilPorId(Integer id) {
        return repoPerfil.findById(id).orElse(null);
    }

    public PerfilUsuarioDTO obtenerPerfilDTO(Integer id) {
        PerfilUsario p = buscarPerfilPorId(id);
        return convertirADTO(p);
    }

    // ---------------- GUARDAR ----------------
    @Transactional
    public PerfilUsuarioDTO guardarPerfil(CrearPerfilRequestDTO dto) {
        PerfilUsario perfil = new PerfilUsario();
        perfil.setNombrePerfil(dto.getNombrePerfil());
        perfil.setDescripcion(dto.getDescripcion());

        PerfilUsario guardado = repoPerfil.save(perfil);
        return convertirADTO(guardado);
    }

    // ---------------- ACTUALIZAR ----------------
   @Transactional
    public PerfilUsuarioDTO actualizarPerfil(Integer id, CrearPerfilRequestDTO dtoRequest) {
        PerfilUsario perfil = repoPerfil.findById(id).orElse(null);
        if (perfil == null) return null;

        if (dtoRequest.getDescripcion() != null) perfil.setDescripcion(dtoRequest.getDescripcion());
        if (dtoRequest.getNombrePerfil() != null) perfil.setNombrePerfil(dtoRequest.getNombrePerfil());

        PerfilUsario actualizado = repoPerfil.save(perfil);
        return convertirADTO(actualizado);
    }


    // ---------------- ELIMINAR ----------------
    @Transactional          
    public boolean eliminarPerfil(Integer id) {
        if (repoPerfil.existsById(id)) {
            repoPerfil.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- USUARIO DEL PERFIL ----------------
    public Usuario obtenerUsuarioDelPerfil(Integer id) {
        PerfilUsario perfil = repoPerfil.findById(id).orElse(null);
        if (perfil == null) return null;
        return perfil.getUsuario(); // puede ser null
    }

    // ---------------- CONVERSIÓN DTO ----------------
    private PerfilUsuarioDTO convertirADTO(PerfilUsario p) {
        if (p == null) return null;
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();
        dto.setUsuarioId(p.getUsuario() != null ? p.getUsuario().getId() : null);
        dto.setNombrePerfil(p.getNombrePerfil());
        dto.setDescripcion(p.getDescripcion());
        dto.setIdPerfil(p.getId()); // añadir el id Integer al DTO
        return dto;
    }
}
