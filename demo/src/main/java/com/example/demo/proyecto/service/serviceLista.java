package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.ListaDTO;
import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.Producto;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

import jakarta.transaction.Transactional;

@Service
public class serviceLista {

    private final repositoryLista repoLista;
    private final repositoryUsuario repoUsuario;
    private final repositoryProducto repoProducto;

    public serviceLista(repositoryLista repoLista, repositoryUsuario repoUsuario, repositoryProducto repoProducto) {
        this.repoLista = repoLista;
        this.repoUsuario = repoUsuario;
        this.repoProducto = repoProducto;
    }

    // ---------------- LISTAR ----------------
    public List<ListaDTO> listarListasDTO() {
        return repoLista.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    // ---------------- BUSCAR ----------------
    public Lista buscarListaPorId(Long id) {
        return repoLista.findById(id).orElse(null);
    }

    public ListaDTO obtenerListaDTO(Long id) {
        Lista l = buscarListaPorId(id);
        return convertirADTO(l);
    }

    // ---------------- GUARDAR ----------------
    @Transactional
    public ListaDTO guardarLista(ListaDTO dto) {
        Lista lista = new Lista();
        lista.setCodLista(dto.getCodLista());

        // Usuario dueño
        if (dto.getUsuarioDuenoId() != null) {
            Usuario u = repoUsuario.findById(dto.getUsuarioDuenoId()).orElse(null);
            lista.setUsuarioDueno(u);
        }

        // Productos
        if (dto.getProductosEnLista() != null) {
            List<Producto> productos = new ArrayList<>();
            for (Long idProd : dto.getProductosEnLista()) {
                Producto p = repoProducto.findById(idProd).orElse(null);
                if (p != null) productos.add(p);
            }
            lista.setProductosEnLista(productos);
        }

        // Usuarios compartidos
        if (dto.getUsuariosCompartida() != null) {
            List<Usuario> usuarios = new ArrayList<>();
            for (Long idUsuario : dto.getUsuariosCompartida()) {
                Usuario u = repoUsuario.findById(idUsuario).orElse(null);
                if (u != null) usuarios.add(u);
            }
            lista.setUsuariosCompartida(usuarios);
        }

        Lista guardada = repoLista.save(lista);
        return convertirADTO(guardada);
    }

    // ---------------- ACTUALIZAR ----------------
    @Transactional
    public ListaDTO actualizarLista(Long id, ListaDTO dto) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null) return null;

        // Actualizar productos si vienen en DTO
        if (dto.getProductosEnLista() != null) {
            List<Producto> productos = new ArrayList<>();
            for (Long idProd : dto.getProductosEnLista()) {
                Producto p = repoProducto.findById(idProd).orElse(null);
                if (p != null) productos.add(p);
            }
            lista.setProductosEnLista(productos);
        }

        // Actualizar usuarios compartidos
        if (dto.getUsuariosCompartida() != null) {
            List<Usuario> usuarios = new ArrayList<>();
            for (Long idUsuario : dto.getUsuariosCompartida()) {
                Usuario u = repoUsuario.findById(idUsuario).orElse(null);
                if (u != null) usuarios.add(u);
            }
            lista.setUsuariosCompartida(usuarios);
        }

        Lista actualizado = repoLista.save(lista);
        return convertirADTO(actualizado);
    }

    // ---------------- ELIMINAR ----------------
    @Transactional
    public boolean eliminarLista(Long id) {
        if (repoLista.existsById(id)) {
            repoLista.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- OBTENER USUARIOS ----------------
    public List<Usuario> obtenerUsuariosDeLista(Long id) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null) return new ArrayList<>();
        return lista.getUsuariosCompartida();
    }

    public List<Producto> obtenerProductosDeLista(Long id) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null) return new ArrayList<>();
        return lista.getProductosEnLista();
    }

    // ---------------- CONVERSIÓN DTO ----------------
    private ListaDTO convertirADTO(Lista l) {
        if (l == null) return null;
        ListaDTO dto = new ListaDTO();
        dto.setCodLista(l.getCodLista());
        dto.setUsuarioDuenoId(l.getUsuarioDueno() != null ? l.getUsuarioDueno().getId() : null);
        if (l.getProductosEnLista() != null)
            dto.setProductosEnLista(l.getProductosEnLista().stream().map(Producto::getId).toList());
        if (l.getUsuariosCompartida() != null)
            dto.setUsuariosCompartida(l.getUsuariosCompartida().stream().map(Usuario::getId).toList());
        return dto;
    }
}
