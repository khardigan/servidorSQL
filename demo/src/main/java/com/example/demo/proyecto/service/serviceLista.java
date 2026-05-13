package com.example.demo.proyecto.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import java.util.Comparator;

import com.example.demo.proyecto.dto.CrearListaRequestDTO;
import com.example.demo.proyecto.dto.ListaDTO;
import com.example.demo.proyecto.dto.ListaDetalleDTO;
import com.example.demo.proyecto.dto.ProductoEstadoDTO;
import com.example.demo.proyecto.dto.ProductoPropioDTO;
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

    // Comparadores oficiales para mantener el orden en todo el sistema
    private static final Comparator<ProductoEstadoDTO> COMPARADOR_PRODUCTOS = Comparator
            .comparing(ProductoEstadoDTO::getSupermercado, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(ProductoEstadoDTO::getPrecio);

    private static final Comparator<ProductoPropioDTO> COMPARADOR_PROPIOS = Comparator
            .comparing(ProductoPropioDTO::getSupermercado, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(p -> p.getPrecioObjetivo() != null ? p.getPrecioObjetivo() : 0.0);

    // Crea un código de 6 letras y números al azar.
    public String generarCodigoAleatorio() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        while (sb.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // ---------------- LISTAR ----------------
    // Devuelve todas las listas que hay en el sistema.
    public List<ListaDTO> listarListasDTO() {
        return repoLista.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    // ---------------- BUSCAR ----------------
    // Busca una lista por su clave interna (ID).
    public Lista buscarListaPorId(Long id) {
        if (id == null)
            return null;
        return repoLista.findById(id).orElse(null);
    }

    // Te da los datos de una lista en formato DTO.
    public ListaDTO obtenerListaDTO(Long id) {
        Lista l = buscarListaPorId(id);
        return convertirADTO(l);
    }

    /**
     * Busca una lista por su código de invitación (6 caracteres).
     */
    public ListaDTO obtenerListaDTO(String codigo) {
        Lista l = repoLista.findByCodigo(codigo).orElse(null);
        return l != null ? convertirADTO(l) : null;
    }

    // ---------------- GUARDAR ----------------
    @Transactional
    // Crea una lista nueva y le asigna un dueño.
    public ListaDTO guardarLista(CrearListaRequestDTO request, Usuario usuarioDueno) {
        Lista lista = new Lista();
        lista.setUsuarioDueno(usuarioDueno);
        lista.setNombre(request.getNombre() != null ? request.getNombre() : "Lista de " + usuarioDueno.getNombre());
        lista.setPublicada(false);
        lista.setCodigo(generarCodigoAleatorio());

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
    // Cambia los datos (nombre, productos, integrantes) de una lista.
    public ListaDTO actualizarLista(Long id, CrearListaRequestDTO request) {
        if (id == null)
            return null;
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null)
            return null;

        if (request.getNombre() != null) {
            lista.setNombre(request.getNombre());
        }

        if (request.getNombre() != null) {
            lista.setNombre(request.getNombre());
        }

        if (request.getProductosEnLista() != null) {
            // Usamos un set de los IDs que queremos tener al final
            Set<Long> nuevosIds = new HashSet<>(request.getProductosEnLista());

            // 1. Eliminar los que ya no están en la lista
            lista.getProductosEnLista().removeIf(lp -> !nuevosIds.contains(lp.getProducto().getId()));

            // 2. Identificar cuáles de los nuevos ya existen para no duplicarlos
            Set<Long> idsExistentes = lista.getProductosEnLista().stream()
                    .map(lp -> lp.getProducto().getId())
                    .collect(Collectors.toSet());

            // 3. Añadir solo los que faltan
            for (Long idProd : nuevosIds) {
                if (!idsExistentes.contains(idProd)) {
                    Producto p = repoProducto.findById(idProd).orElse(null);
                    if (p != null) {
                        ListaProducto lp = new ListaProducto();
                        lp.setLista(lista);
                        lp.setProducto(p);
                        lp.setComprado(false);
                        lp.setCantidad(1);
                        lista.getProductosEnLista().add(lp);
                    }
                }
            }
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
    @Transactional
    // Te da el detalle de todas las listas donde tú participas.
    public List<ListaDetalleDTO> obtenerMisListasDetalle(Usuario usuario) {
        // Obtenemos todas las listas
        // Esta función filtra las listas generales para devolver solo en las que el
        // usuario es propietario o integrante
        List<Lista> todasListas = repoLista.findAll();

        // Filtramos las que son del usuario o en las que está compartido
        List<Lista> misListas = todasListas.stream()
                .filter(l -> l.getUsuarioDueno().getId().equals(usuario.getId()) ||
                        (l.getUsuariosCompartida() != null && l.getUsuariosCompartida().stream()
                                .anyMatch(u -> u.getId().equals(usuario.getId()))))
                .toList();

        return misListas.stream().map(this::convertirAListaDetalle).collect(Collectors.toList());
    }

    // Pasa una lista a un formato con mucho detalle (dueño, productos, etc).
    private ListaDetalleDTO convertirAListaDetalle(Lista l) {
        // Mapea la entidad Lista a un DTO estructurado que junta el perfil del dueño,
        // los integrantes con sus alias ("nick")
        // y los detalles de cada producto de esa lista incluyendo su cantidad y si ha
        // sido comprado.
        ListaDetalleDTO dto = new ListaDetalleDTO();
        dto.setCodLista(l.getCodLista());
        dto.setUsuarioDuenoId(l.getUsuarioDueno() != null ? l.getUsuarioDueno().getId() : null);
        dto.setNombre(l.getNombre());
        dto.setPublicada(Boolean.TRUE.equals(l.getPublicada()));

        // Manejo del código de invitación (Migración perezosa si no existe)
        if (l.getCodigo() == null) {
            l.setCodigo(generarCodigoAleatorio());
            // No hace falta save si estamos en una transacción activa (@Transactional)
        }
        dto.setCodigo(l.getCodigo());

        // Intentar obtener el nick del perfil, sino el nombre de usuario
        String nick = l.getUsuarioDueno() != null ? l.getUsuarioDueno().getNombre() : "Desconocido";
        if (l.getUsuarioDueno() != null && l.getUsuarioDueno().getPerfilUsuario() != null
                && l.getUsuarioDueno().getPerfilUsuario().getNombrePerfil() != null) {
            nick = l.getUsuarioDueno().getPerfilUsuario().getNombrePerfil();
        }
        dto.setNombreDuenoNick(nick);
        if (l.getUsuarioDueno() != null && l.getUsuarioDueno().getPerfilUsuario() != null) {
            dto.setImagenDuenoUrl(l.getUsuarioDueno().getPerfilUsuario().getImagenUrl());
        }

        // Usuarios compartida
        if (l.getUsuariosCompartida() != null) {
            List<UsuarioMinimoDTO> usuariosDto = l.getUsuariosCompartida().stream().map(u -> {
                UsuarioMinimoDTO uDto = new UsuarioMinimoDTO();
                uDto.setId(u.getId());
                String uNick = u.getNombre();
                String uImg = null;
                if (u.getPerfilUsuario() != null) {
                    if (u.getPerfilUsuario().getNombrePerfil() != null) {
                        uNick = u.getPerfilUsuario().getNombrePerfil();
                    }
                    uImg = u.getPerfilUsuario().getImagenUrl();
                }
                uDto.setNick(uNick);
                uDto.setImagenUrl(uImg);
                return uDto;
            }).toList();
            dto.setUsuariosCompartida(usuariosDto);
        } else {
            dto.setUsuariosCompartida(new ArrayList<>());
        }

        // Productos
        if (l.getProductosEnLista() != null) {
            List<ProductoEstadoDTO> prodsDto = l.getProductosEnLista().stream()
                    .map(lp -> {
                        ProductoEstadoDTO pDto = new ProductoEstadoDTO();
                        Producto p = lp.getProducto();
                        pDto.setId(p.getId());
                        pDto.setNombre(p.getNombre());
                        pDto.setPrecio(p.getPrecio());
                        pDto.setCantidad(lp.getCantidad() != null ? lp.getCantidad() : 1);
                        pDto.setComprado(Boolean.TRUE.equals(lp.getComprado()));
                        pDto.setSupermercado(p.getSupermercado());
                        return pDto;
                    })
                    .sorted(COMPARADOR_PRODUCTOS)
                    .toList();
            dto.setProductos(prodsDto);
        } else {
            dto.setProductos(new ArrayList<>());
        }

        // Productos propios asociados a esta lista
        if (l.getProductoPropios() != null) {
            List<ProductoPropioDTO> propiosDto = l.getProductoPropios().stream()
                    .map(this::convertirAProductoPropioDTO)
                    .sorted(COMPARADOR_PROPIOS)
                    .collect(Collectors.toList());
            dto.setProductoPropios(propiosDto);
        } else {
            dto.setProductoPropios(new ArrayList<>());
        }

        return dto;
    }

    // ---------------- ELIMINAR ----------------
    @Transactional
    // Borra una lista por completo por su ID.
    public boolean eliminarLista(Long id) {
        if (id == null)
            return false;
        if (repoLista.existsById(id)) {
            repoLista.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------------- OBTENER USUARIOS ----------------
    // Te dice qué personas están invitadas a una lista.
    public List<Usuario> obtenerUsuariosDeLista(Long id) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null)
            return new ArrayList<>();
        return lista.getUsuariosCompartida();
    }

    // Te dice qué productos hay guardados en una lista.
    public List<ListaProducto> obtenerProductosDeLista(Long id) {
        Lista lista = repoLista.findById(id).orElse(null);
        if (lista == null)
            return new ArrayList<>();
        return lista.getProductosEnLista();
    }

    // ---------------- PUBLICAR / DESPUBLICAR ----------------
    @Transactional
    // Hace que una lista sea pública o privada.
    public boolean cambiarEstadoPublicacion(Long id, boolean estado) {
        Lista l = repoLista.findById(id).orElse(null);
        if (l == null)
            return false;

        Usuario listasPublicas = repoUsuario.findByNombreIgnoreCase("ListasPublicas");

        if (estado) {
            // PUBLICAR
            if (listasPublicas != null) {
                // Buscamos si ya existe un snapshot de ESTA lista específica
                // Buscamos por el patrón de metadata que incluya el ID de la lista original
                String metaSearch = "orig=" + l.getCodLista() + "|";
                Optional<Lista> snapshotExistente = repoLista.findAll().stream()
                        .filter(lista -> "ListasPublicas".equals(lista.getUsuarioDueno().getNombre()) &&
                                lista.getNombre() != null && lista.getNombre().contains(metaSearch))
                        .findFirst();

                if (snapshotExistente.isEmpty()) {
                    // No hay snapshot, lo creamos
                    ListaDTO copiaDto = clonarLista(id, listasPublicas);
                    if (copiaDto != null) {
                        Lista copia = repoLista.findById(copiaDto.getCodLista()).orElse(null);
                        if (copia != null) {
                            copia.setPublicada(true);
                            // Metadata: id del dueño y ID de la lista original para poder encontrarla luego
                            copia.setNombre(l.getNombre() + " |META:id=" + l.getUsuarioDueno().getId() +
                                    ",nick=" + l.getUsuarioDueno().getNombre() +
                                    ",orig=" + l.getCodLista() + "|");
                            repoLista.save(copia);
                        }
                    }
                }
            }
        } else {
            // DESPUBLICAR
            if (listasPublicas != null) {
                String metaSearch = "orig=" + l.getCodLista() + "|";
                repoLista.findAll().stream()
                        .filter(lista -> "ListasPublicas".equals(lista.getUsuarioDueno().getNombre()) &&
                                lista.getNombre() != null && lista.getNombre().contains(metaSearch))
                        .forEach(snapshot -> repoLista.delete(snapshot));
            }
        }

        l.setPublicada(estado);
        repoLista.save(l);
        return true;
    }

    @Transactional
    // Cambia el nombre de la lista.
    public boolean actualizarNombre(Long id, String nuevoNombre) {
        if (id == null)
            return false;
        Lista l = repoLista.findById(id).orElse(null);
        if (l == null)
            return false;
        l.setNombre(nuevoNombre);
        repoLista.save(l);
        return true;
    }

    // ---------------- LISTAR PÚBLICAS ----------------
    @Transactional
    // Te da todas las listas que la gente ha hecho públicas (mostramos solo las
    // guardadas en ListasPublicas).
    public List<ListaDetalleDTO> obtenerListasPublicas() {
        return repoLista.findAll().stream()
                .filter(l -> Boolean.TRUE.equals(l.getPublicada())
                        && "ListasPublicas".equals(l.getUsuarioDueno().getNombre()))
                .map(l -> {
                    ListaDetalleDTO dto = convertirAListaDetalle(l);
                    String nombreReal = dto.getNombre();
                    if (nombreReal != null && nombreReal.contains("|META:")) {
                        try {
                            int startMeta = nombreReal.indexOf("|META:");
                            int endMeta = nombreReal.indexOf("|", startMeta + 6);
                            if (endMeta != -1) {
                                String metaContent = nombreReal.substring(startMeta + 6, endMeta);
                                String[] parts = metaContent.split(",");
                                for (String p : parts) {
                                    if (p.startsWith("id=")) {
                                        Long idAutor = Long.parseLong(p.substring(3));
                                        dto.setUsuarioDuenoId(idAutor);
                                        // Buscamos su perfil real para tener la foto actualizada
                                        Usuario autor = repoUsuario.findById(idAutor).orElse(null);
                                        if (autor != null && autor.getPerfilUsuario() != null) {
                                            dto.setImagenDuenoUrl(autor.getPerfilUsuario().getImagenUrl());
                                            if (autor.getPerfilUsuario().getNombrePerfil() != null) {
                                                dto.setNombreDuenoNick(autor.getPerfilUsuario().getNombrePerfil());
                                            }
                                        }
                                    }
                                    if (p.startsWith("nick=") && dto.getNombreDuenoNick() == null) {
                                        dto.setNombreDuenoNick(p.substring(5));
                                    }
                                }
                                dto.setNombre(nombreReal.substring(0, startMeta).trim());
                            }
                        } catch (Exception e) {
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ---------------- CLONAR LISTA ----------------
    @Transactional
    // Hace una copia de una lista para otro usuario.
    public ListaDTO clonarLista(Long idOriginal, Usuario nuevoDueno) {
        Lista original = repoLista.findById(idOriginal).orElse(null);
        if (original == null)
            return null;

        Lista nueva = new Lista();
        nueva.setUsuarioDueno(nuevoDueno);
        nueva.setNombre("Copia de " + (original.getNombre() != null ? original.getNombre() : "Lista"));
        nueva.setPublicada(false);
        nueva.setCodigo(generarCodigoAleatorio());

        // Copiamos los productos (incluyendo cantidades)
        if (original.getProductosEnLista() != null) {
            List<ListaProducto> nuevosProds = original.getProductosEnLista().stream()
                    .map(lp -> {
                        ListaProducto nLp = new ListaProducto();
                        nLp.setLista(nueva);
                        nLp.setProducto(lp.getProducto());
                        nLp.setCantidad(lp.getCantidad() != null ? lp.getCantidad() : 1);
                        nLp.setComprado(false); // Al clonar, nada está comprado
                        return nLp;
                    }).collect(Collectors.toList());
            nueva.setProductosEnLista(nuevosProds);
        }

        // No copiamos los usuarios compartidos por defecto al clonar para uno mismo

        Lista guardada = repoLista.save(nueva);
        return convertirADTO(guardada);
    }

    // Suma el precio de todos los productos de la lista.
    public Double calcularTotalLista(Long id) {
        Lista lista = buscarListaPorId(id);
        if (lista == null || lista.getProductosEnLista() == null)
            return 0.0;
        return lista.getProductosEnLista().stream()
                .mapToDouble(lp -> {
                    if (lp.getProducto() == null || lp.getProducto().getPrecio() == null)
                        return 0.0;
                    int cant = lp.getCantidad() != null ? lp.getCantidad() : 1;
                    return lp.getProducto().getPrecio() * cant;
                })
                .sum();
    }

    // ---------------- MARCAR COMPRADO ----------------
    @Transactional
    // Marca si ya has echado un producto al carro.
    public boolean marcarProductoComoComprado(Long listaId, Long productoId, boolean estado, Usuario usuario) {
        if (listaId == null || productoId == null)
            return false;
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

    // ---------------- ACTUALIZAR CANTIDAD ----------------
    @Transactional
    // Cambia cuántas unidades quieres de un producto.
    public boolean actualizarCantidadProducto(Long listaId, Long productoId, int cantidad, Usuario usuario) {
        if (listaId == null || productoId == null)
            return false;
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
                lp.setCantidad(cantidad > 0 ? cantidad : 1);
                repoLista.save(lista);
                return true;
            }
        }
        return false;
    }

    /**
     * Permite a un usuario unirse a una lista compartida mediante su código.
     * 
     * @param codigo  Código de 6 caracteres de la lista.
     * @param usuario Usuario que quiere unirse.
     * @return true si se unió con éxito, false si no se encontró la lista o ya
     *         estaba en ella.
     */
    @Transactional
    // Te permite entrar en una lista si tienes el código de 6 letras.
    public boolean unirseAListaPorCodigo(String codigo, Usuario usuario) {
        Optional<Lista> opt = repoLista.findByCodigo(codigo);
        System.out.println("DEBUG: Intentando unir usuario " + usuario.getNombre() + " a lista con código " + codigo);
        if (opt.isEmpty())
            return false;

        Lista lista = opt.get();

        // Un dueño no puede unirse a su propia lista como invitado
        if (lista.getUsuarioDueno().getId().equals(usuario.getId())) {
            throw new RuntimeException("Ya eres el dueño de esta lista");
        }

        // Inicializar la lista de compartidos si es nula
        if (lista.getUsuariosCompartida() == null) {
            lista.setUsuariosCompartida(new ArrayList<>());
        }

        // Verificar si el usuario ya es integrante de la lista
        boolean yaEsIntegrante = lista.getUsuariosCompartida().stream()
                .anyMatch(u -> u.getId().equals(usuario.getId()));

        if (yaEsIntegrante) {
            throw new RuntimeException("Ya perteneces a esta lista");
        }

        // Añadir usuario y persistir cambios
        lista.getUsuariosCompartida().add(usuario);
        repoLista.save(lista);
        return true;
    }

    // ---------------- CONVERSIÓN DTO ----------------
    // Pasa una lista del modelo al formato DTO básico.
    private ListaDTO convertirADTO(Lista l) {
        if (l == null)
            return null;
        ListaDTO dto = new ListaDTO();
        dto.setCodLista(l.getCodLista());
        if (l.getUsuarioDueno() != null) {
            dto.setUsuarioDuenoId(l.getUsuarioDueno().getId());
            dto.setNombreDueno(l.getUsuarioDueno().getNombre());
            if (l.getUsuarioDueno().getPerfilUsuario() != null) {
                dto.setNickDueno(l.getUsuarioDueno().getPerfilUsuario().getNombrePerfil());
                dto.setImagenDuenoUrl(l.getUsuarioDueno().getPerfilUsuario().getImagenUrl());
            } else {
                dto.setNickDueno(l.getUsuarioDueno().getNombre());
            }
        }
        dto.setNombre(l.getNombre());
        dto.setPublicada(Boolean.TRUE.equals(l.getPublicada()));
        dto.setCodigo(l.getCodigo());
        if (l.getProductosEnLista() != null)
            dto.setProductosEnLista(l.getProductosEnLista().stream().map(lp -> lp.getProducto().getId()).toList());
        if (l.getUsuariosCompartida() != null)
            dto.setUsuariosCompartida(l.getUsuariosCompartida().stream().map(Usuario::getId).toList());
        return dto;
    }

    // Pasa un producto inventado al formato DTO.
    private com.example.demo.proyecto.dto.ProductoPropioDTO convertirAProductoPropioDTO(
            com.example.demo.proyecto.model.ProductoPropio pp) {
        com.example.demo.proyecto.dto.ProductoPropioDTO dto = new com.example.demo.proyecto.dto.ProductoPropioDTO();
        dto.setId(pp.getId());
        dto.setNombre(pp.getNombre());
        dto.setPrecioObjetivo(pp.getPrecioObjetivo());
        dto.setNotas(pp.getNotas());
        dto.setSupermercado(pp.getSupermercado());
        dto.setListaId(pp.getLista() != null ? pp.getLista().getCodLista() : null);
        dto.setCantidad(pp.getCantidad() != null ? pp.getCantidad() : 1);
        dto.setComprado(pp.getComprado()); // Estado de comprado
        dto.setUsuarioId(pp.getUsuario() != null ? pp.getUsuario().getId() : null); // Dueño del producto
        dto.setCreatedAt(pp.getCreatedAt());
        return dto;
    }
}
