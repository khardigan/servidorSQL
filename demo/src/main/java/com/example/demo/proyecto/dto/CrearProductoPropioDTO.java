package com.example.demo.proyecto.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearProductoPropioDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private Double precioObjetivo;

    private String notas;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioObjetivo() { return precioObjetivo; }
    public void setPrecioObjetivo(Double precioObjetivo) { this.precioObjetivo = precioObjetivo; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
