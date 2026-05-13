package com.example.demo.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para crear o editar un perfil. Tiene que recibir nombre, descripción e ID del usuario.
public class CrearPerfilRequestDTO {

    private String nombrePerfil;

    private String descripcion;

    private String subtitulo;
    private String fechaNacimiento;
    private String edad;
    private String residencia;
    private String email;
    private String telefono;

    @NotNull(message = "El usuario asociado es obligatorio")
    private Long usuarioId;

    private String imagenUrl;

    // Getters y Setters
    public String getNombrePerfil() { return nombrePerfil; }
    public void setNombrePerfil(String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }
    public String getResidencia() { return residencia; }
    public void setResidencia(String residencia) { this.residencia = residencia; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
