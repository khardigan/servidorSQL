package com.example.demo.proyecto.dto;

import java.util.List;

public class ListaDetalleDTO {
    private Long codLista;
    private Long usuarioDuenoId;
    private String nombreDuenoNick;
    private List<UsuarioMinimoDTO> usuariosCompartida;
    private List<ProductoEstadoDTO> productos;
    private List<ProductoPropioDTO> productoPropios;
    private String nombre;
    private boolean publicada;

    public List<ProductoPropioDTO> getProductoPropios() {
        return productoPropios;
    }

    public void setProductoPropios(List<ProductoPropioDTO> productoPropios) {
        this.productoPropios = productoPropios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPublicada() {
        return publicada;
    }

    public void setPublicada(boolean publicada) {
        this.publicada = publicada;
    }

    public Long getCodLista() {
        return codLista;
    }

    public void setCodLista(Long codLista) {
        this.codLista = codLista;
    }

    public Long getUsuarioDuenoId() {
        return usuarioDuenoId;
    }

    public void setUsuarioDuenoId(Long usuarioDuenoId) {
        this.usuarioDuenoId = usuarioDuenoId;
    }

    public String getNombreDuenoNick() {
        return nombreDuenoNick;
    }

    public void setNombreDuenoNick(String nombreDuenoNick) {
        this.nombreDuenoNick = nombreDuenoNick;
    }

    public List<UsuarioMinimoDTO> getUsuariosCompartida() {
        return usuariosCompartida;
    }

    public void setUsuariosCompartida(List<UsuarioMinimoDTO> usuariosCompartida) {
        this.usuariosCompartida = usuariosCompartida;
    }

    public List<ProductoEstadoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoEstadoDTO> productos) {
        this.productos = productos;
    }

    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
