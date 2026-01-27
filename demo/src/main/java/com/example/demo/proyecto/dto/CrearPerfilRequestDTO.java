package com.example.demo.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearPerfilRequestDTO {

    @NotBlank(message = "El nombre del perfil no puede estar vacío")
    private String nombrePerfil;

    @NotBlank(message = "La descripción del perfil no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El usuario asociado es obligatorio")
    private Long usuarioId;

    // Getters y Setters
    public String getNombrePerfil() { return nombrePerfil; }
    public void setNombrePerfil(String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
