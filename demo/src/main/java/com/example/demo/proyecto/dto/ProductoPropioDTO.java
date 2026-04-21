package com.example.demo.proyecto.dto;

import java.time.LocalDateTime;

public class ProductoPropioDTO {
    private Long id;
    private String nombre;
    private Double precioObjetivo;
    private String notas;
    private Long listaId;
    private String supermercado;
    private Integer cantidad;
    private LocalDateTime createdAt;

    // Getters y Setters
    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

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

    public Double getPrecioObjetivo() {
        return precioObjetivo;
    }

    public void setPrecioObjetivo(Double precioObjetivo) {
        this.precioObjetivo = precioObjetivo;
    }

    public Long getListaId() {
        return listaId;
    }

    public void setListaId(Long listaId) {
        this.listaId = listaId;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getSupermercado() {
        return supermercado;
    }

    public void setSupermercado(String supermercado) {
        this.supermercado = supermercado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
