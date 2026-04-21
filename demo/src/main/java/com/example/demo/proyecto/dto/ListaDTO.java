package com.example.demo.proyecto.dto;

import java.util.List;

public class ListaDTO {
    private Long codLista;
    private Long usuarioDuenoId;
    private List<Long> usuariosCompartida;   // IDs de usuarios
    private List<Long> productosEnLista;      // IDs de productos
    private String nombre;
    private boolean publicada;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isPublicada() { return publicada; }
    public void setPublicada(boolean publicada) { this.publicada = publicada; }
    public Long getCodLista() { return codLista; }
    public void setCodLista(Long codLista) { this.codLista = codLista; }
    public Long getUsuarioDuenoId() { return usuarioDuenoId; }
    public void setUsuarioDuenoId(Long usuarioDuenoId) { this.usuarioDuenoId = usuarioDuenoId; }
    public List<Long> getUsuariosCompartida() { return usuariosCompartida; }
    public void setUsuariosCompartida(List<Long> usuariosCompartida) { this.usuariosCompartida = usuariosCompartida; }
    public List<Long> getProductosEnLista() { return productosEnLista; }
    public void setProductosEnLista(List<Long> productosEnLista) { this.productosEnLista = productosEnLista; }

    private String codigo;
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
