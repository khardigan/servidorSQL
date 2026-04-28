package com.example.demo.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CrearComentarioDTO {

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 1000, message = "El comentario es demasiado largo")
    private String contenido;
    private Double puntuacion;
    private Long idUsuario;
    private Long idProducto;

    public CrearComentarioDTO() {
    }

    public CrearComentarioDTO(String contenido, Double puntuacion, Long idUsuario, Long idProducto) {
        this.contenido = contenido;
        this.puntuacion = puntuacion;
        this.idUsuario = idUsuario;
        this.idProducto = idProducto;
    }

    public String getContenido() {
        return contenido;
    }
    public Long getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

}
