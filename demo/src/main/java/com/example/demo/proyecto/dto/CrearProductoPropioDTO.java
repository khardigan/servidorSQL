package com.example.demo.proyecto.dto;

import jakarta.validation.constraints.NotBlank;

// DTO de entrada para añadir un producto personalizado a una lista. Tiene que recibir nombre, precio y cantidad.
public class CrearProductoPropioDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private Double precioObjetivo;
    private Long listaId;
    private String notas;
    private String supermercado;

    // Getters y Setters
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

    private Integer cantidad;

    private Boolean comprado;

    public Boolean getComprado() {
        return comprado;
    }

    public void setComprado(Boolean comprado) {
        this.comprado = comprado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
