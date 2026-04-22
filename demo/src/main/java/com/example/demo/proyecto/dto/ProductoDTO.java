package com.example.demo.proyecto.dto;

import java.util.List;

// DTO de salida con los datos detallados de un producto.
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private int cantidad;
    private Long usuarioRegistradorId;
    private List<Long> listas;
    private boolean confirmado;
    private String supermercado;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Long getUsuarioRegistradorId() {
        return usuarioRegistradorId;
    }

    public void setUsuarioRegistradorId(Long usuarioRegistradorId) {
        this.usuarioRegistradorId = usuarioRegistradorId;
    }

    public List<Long> getListas() {
        return listas;
    }

    public void setListas(List<Long> listas) {
        this.listas = listas;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public String getSupermercado() {
        return supermercado;
    }

    public void setSupermercado(String supermercado) {
        this.supermercado = supermercado;
    }

}
