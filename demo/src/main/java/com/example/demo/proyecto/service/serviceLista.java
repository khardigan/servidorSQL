package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.proyecto.dto.CrearListaRequestDTO;
import com.example.demo.proyecto.dto.ListaDTO;
import com.example.demo.proyecto.dto.ListaDetalleDTO;
import com.example.demo.proyecto.dto.ProductoEstadoDTO;
import com.example.demo.proyecto.dto.UsuarioMinimoDTO;
import com.example.demo.proyecto.model.Lista;
import com.example.demo.proyecto.model.ListaProducto;
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
    public ListaDTO guardarLista(CrearListaRequestDTO request, Usuario usuarioDueno) {
        Lista lista = new Lista();
        lista.setUsuarioDueno(usuarioDueno);

        if (request.getProductosEnLista() != null) {
            List<ListaProducto> productos = request.getProductosEnLista().stream()
                    .map(idProd -> {
                        Producto p = repoProducto.findById(idProd).orElse(null);
                        if (p == null)
                            return null;
                        ListaProducto lp = new ListaProducto();
                        lp.setLista(lista);
                        lp.setProducto(p);
                        lp.setComprado(false); // Por defecto al agregar a lista no está comprado
                        return lp;
                    })
                    .filter(lp -> lp != null).collect(Collectors.toList());
            lista.setProductosEnLista(productos);
        }
        if (request.getUsuariosCompartida() != null) {
            List<Usuario> usuarios = request.getUsuariosCompartida().stream()
                    .map(idU -> repoUsuario.findById(idU).orElse(null))
                    .filter(u -> u != null).collect(Collectors.toList());
            lista.setUsuariosCompartida(usuarios);
        }

        Lista guardada = repoLista.save(lista);
        return convertirADTO(guardada);
    }

    // ---------------- ACTUALIZAR ----------------
    @Transactional
    public ListaDTO actualizarLista(Long id, CrearListaRequestDTO request) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null)
            return null;

        if (request.getProductosEnLista() != null) {
            if (lista.getProductosEnLista() != null) {
                lista.getProductosEnLista().clear();
            } else {
                lista.setProductosEnLista(new ArrayList<>());
            }
            List<ListaProducto> productos = request.getProductosEnLista().stream()
                    .map(idProd -> {
                        Producto p = repoProducto.findById(idProd).orElse(null);
                        if (p == null)
                            return null;
                        ListaProducto lp = new ListaProducto();
                        lp.setLista(lista);
                        lp.setProducto(p);
                        lp.setComprado(false);
                        return lp;
                    })
                    .filter(lp -> lp != null).collect(Collectors.toList());
            lista.getProductosEnLista().addAll(productos);
        }

        if (request.getUsuariosCompartida() != null) {
            List<Usuario> usuarios = request.getUsuariosCompartida().stream()
                    .map(idU -> repoUsuario.findById(idU).orElse(null))
                    .filter(u -> u != null).collect(Collectors.toList());
            lista.setUsuariosCompartida(usuarios);
        }

        Lista actualizado = repoLista.save(lista);
        return convertirADTO(actualizado);
    }

    // ---------------- OBTENER MIS LISTAS DETALLE ----------------
    public List<ListaDetalleDTO> obtenerMisListasDetalle(Usuario usuario) {
        // Obtenemos todas las listas
        // Esta función filtra las listas generales para devolver solo en las que el
        // usuario es propietario o integrante
        List<Lista> todasListas = repoLista.findAll();

        // Filtramos las que son del usuario o en las que está compartido
        List<Lista> misListas = todasListas.stream()
                .filter(l -> l.getUsuarioDueno().getId().equals(usuario.getId()) ||
                        (l.getUsuariosCompartida() != null && l.getUsuariosCompartida().contains(usuario)))
                .toList();

        return misListas.stream().map(this::convertirAListaDetalle).collect(Collectors.toList());
    }

    private ListaDetalleDTO convertirAListaDetalle(Lista l) {
        // Mapea la entidad Lista a un DTO estructurado que junta el perfil del dueño,
        // los integrantes con sus alias ("nick")
        // y los detalles de cada producto de esa lista incluyendo su cantidad y si ha
        // sido comprado.
        ListaDetalleDTO dto = new ListaDetalleDTO();
        dto.setCodLista(l.getCodLista());
        dto.setUsuarioDuenoId(l.getUsuarioDueno().getId());

        // Intentar obtener el nick del perfil, sino el nombre de usuario
        String nick = l.getUsuarioDueno().getNombre();
        if (l.getUsuarioDueno().getPerfilUsuario() != null
                && l.getUsuarioDueno().getPerfilUsuario().getNombrePerfil() != null) {
            nick = l.getUsuarioDueno().getPerfilUsuario().getNombrePerfil();
        }
        dto.setNombreDuenoNick(nick);

        // Usuarios compartida
        if (l.getUsuariosCompartida() != null) {
            List<UsuarioMinimoDTO> usuariosDto = l.getUsuariosCompartida().stream().map(u -> {
                UsuarioMinimoDTO uDto = new UsuarioMinimoDTO();
                uDto.setId(u.getId());
                String uNick = u.getNombre();
                if (u.getPerfilUsuario() != null && u.getPerfilUsuario().getNombrePerfil() != null) {
                    uNick = u.getPerfilUsuario().getNombrePerfil();
                }
                uDto.setNick(uNick);
                return uDto;
            }).toList();
            dto.setUsuariosCompartida(usuariosDto);
        } else {
            dto.setUsuariosCompartida(new ArrayList<>());
        }

        // Productos
        if (l.getProductosEnLista() != null) {
            List<ProductoEstadoDTO> prodsDto = l.getProductosEnLista().stream().map(lp -> {
                ProductoEstadoDTO pDto = new ProductoEstadoDTO();
                Producto p = lp.getProducto();
                pDto.setId(p.getId());
                pDto.setNombre(p.getNombre());
                pDto.setPrecio(p.getPrecio());
                pDto.setCantidad(p.getCantidad());
                pDto.setComprado(lp.isComprado());
                return pDto;
            }).toList();
            dto.setProductos(prodsDto);
        } else {
            dto.setProductos(new ArrayList<>());
        }

        return dto;
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
        if (lista == null)
            return new ArrayList<>();
        return lista.getUsuariosCompartida();
    }

    public List<ListaProducto> obtenerProductosDeLista(Long id) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null)
            return new ArrayList<>();
        return lista.getProductosEnLista();
    }

    public Double calcularTotalLista(Long id) {
        Lista lista = buscarListaPorId(id);
        if (lista == null || lista.getProductosEnLista() == null)
            return 0.0;
        return lista.getProductosEnLista().stream()
                .mapToDouble(lp -> {
                    if (lp.getProducto() == null || lp.getProducto().getPrecio() == null)
                        return 0.0;
                    return lp.getProducto().getPrecio() * lp.getProducto().getCantidad();
                })
                .sum();
    }

    // ---------------- MARCAR COMPRADO ----------------
    @Transactional
    public boolean marcarProductoComoComprado(Long listaId, Long productoId, boolean estado, Usuario usuario) {
        // Recupera la lista, comprueba que el usuario pertenezca a ella y luego busca y
        // actualiza
        // únicamente el estado del producto requerido, guardándolo en la base de datos
        // intermedia 'ListaProducto'
        Lista lista = repoLista.findById(listaId).orElse(null);
        if (lista == null)
            throw new RuntimeException("Lista no encontrada");

        // Verificar permisos
        if (!lista.getUsuarioDueno().getId().equals(usuario.getId()) &&
                (lista.getUsuariosCompartida() == null || !lista.getUsuariosCompartida().contains(usuario))) {
            throw new RuntimeException("No tienes permisos para editar esta lista");
        }

        if (lista.getProductosEnLista() != null) {
            java.util.Optional<ListaProducto> optLp = lista.getProductosEnLista().stream()
                    .filter(lp -> lp.getProducto().getId().equals(productoId))
                    .findFirst();

            if (optLp.isPresent()) {
                ListaProducto lp = optLp.get();
                lp.setComprado(estado);
                repoLista.save(lista);
                return true;
            }
        }
        return false;
    }

    // ---------------- CONVERSIÓN DTO ----------------
    private ListaDTO convertirADTO(Lista l) {
        if (l == null)
            return null;
        ListaDTO dto = new ListaDTO();
        dto.setCodLista(l.getCodLista());
        dto.setUsuarioDuenoId(l.getUsuarioDueno() != null ? l.getUsuarioDueno().getId() : null);
        if (l.getProductosEnLista() != null)
            dto.setProductosEnLista(l.getProductosEnLista().stream().map(lp -> lp.getProducto().getId()).toList());
        if (l.getUsuariosCompartida() != null)
            dto.setUsuariosCompartida(l.getUsuariosCompartida().stream().map(Usuario::getId).toList());
        return dto;
    }
}
