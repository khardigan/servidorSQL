package com.example.demo.proyecto.dto;

import java.util.List;

// DTO de salida con la información detallada de un usuario.
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String fechaRegistro;
    private List<Long> listaProductosSubidos;
    private Integer idPerfil;
    private Boolean activo;
    private List<Long> listasCreadas;
    private List<Long> listasCompartidas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Long> getListaProductosSubidos() {
        return listaProductosSubidos;
    }

    public void setListaProductosSubidos(List<Long> listaProductosSubidos) {
        this.listaProductosSubidos = listaProductosSubidos;
    }

    public Integer getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
    }

    public List<Long> getListasCreadas() {
        return listasCreadas;
    }

    public void setListasCreadas(List<Long> listasCreadas) {
        this.listasCreadas = listasCreadas;
    }

    public List<Long> getListasCompartidas() {
        return listasCompartidas;
    }

    public void setListasCompartidas(List<Long> listasCompartidas) {
        this.listasCompartidas = listasCompartidas;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
