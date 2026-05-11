package com.example.demo.proyecto.dto;

public class ComentarioDTO {
    private Long id;
    private String contenido;
    private String fecha;
    private Double puntuacion;
    private Long usuarioId;
    private Long productoId;

    public ComentarioDTO() {
    }

    public ComentarioDTO(Long id, String contenido, String fecha, Double puntuacion, Long usuarioId, Long productoId) {
        this.id = id;
        this.contenido = contenido;
        this.fecha = fecha;
        this.puntuacion = puntuacion;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    private String nombreUsuario;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
