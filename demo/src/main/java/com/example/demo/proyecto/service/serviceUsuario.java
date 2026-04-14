package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryUsuario;

@Service
public class serviceUsuario {

    private final repositoryUsuario repoUsuario;

    public serviceUsuario(repositoryUsuario repoUsuario) {
        this.repoUsuario = repoUsuario;
    }

    public List<Usuario> listarUsuarios() {
        return repoUsuario.findAll();
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return repoUsuario.findById(id).orElse(null);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getNombre() != null) {
            boolean existe = repoUsuario.findAll().stream()
                    .anyMatch(u -> u.getNombre() != null && u.getNombre().equals(usuario.getNombre()));
            if (existe) {
                throw new com.example.demo.proyecto.exception.RecursoDuplicadoException("Usuario ya existe en la base de datos");
            }
        }
        return repoUsuario.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datos) {
        datos.setId(id);
        return repoUsuario.save(datos);
    }

   

    public List<Producto> obtenerProductosSubidosPorUsuario(Long id) {
        Usuario u = repoUsuario.findById(id).orElse(null);
        return u == null ? new ArrayList<>() : u.getListaProductosSubidos();
    }

}
