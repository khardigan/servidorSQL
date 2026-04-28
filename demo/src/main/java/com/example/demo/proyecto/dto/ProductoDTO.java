package com.example.demo.proyecto.dto;

import java.util.List;

// DTO de salida con los datos detallados de un producto.
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Long usuarioRegistradorId;
    private List<Long> listas;
    private boolean confirmado;
    private String supermercado;
    private String imagenUrl;
    private String categoria;
    private Double mediaPuntuacion;
    private Long totalComentarios;
    private List<ComentarioDTO> comentarios;

    // Constructor vacío (necesario para frameworks)
    public ProductoDTO() {
    }

    // Constructor para la Query JPQL (Paso 2)
    public ProductoDTO(Long id, String nombre, String descripcion, Double precio,
            String supermercado, String imagenUrl, String categoria, Double mediaPuntuacion, Long totalComentarios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.supermercado = supermercado;
        this.imagenUrl = imagenUrl;
        this.categoria = categoria;
        this.mediaPuntuacion = (mediaPuntuacion != null) ? mediaPuntuacion : 0.0;
        this.totalComentarios = (totalComentarios != null) ? totalComentarios : 0L;
    }

    // ... Mantén tus getters y setters actuales y añade los nuevos:

    public Double getMediaPuntuacion() {
        return mediaPuntuacion;
    }

    public void setMediaPuntuacion(Double mediaPuntuacion) {
        this.mediaPuntuacion = mediaPuntuacion;
    }

    public Long getTotalComentarios() {
        return totalComentarios;
    }

    public void setTotalComentarios(Long totalComentarios) {
        this.totalComentarios = totalComentarios;
    }

    public List<ComentarioDTO> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioDTO> comentarios) {
        this.comentarios = comentarios;
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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}
