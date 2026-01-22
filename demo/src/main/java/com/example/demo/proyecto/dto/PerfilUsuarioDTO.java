package com.example.demo.proyecto.dto;

public class PerfilUsuarioDTO{
    private Integer id_perfil;
    private String nombrePerfil;
    private String descripcion;
    private Long usuarioId;

    // Getters y Setters
    public Integer getIdPerfil() { return id_perfil; }
    public void setIdPerfil(Integer id_perfil) { this.id_perfil = id_perfil; }

    public String getNombrePerfil() { return nombrePerfil; }
    public void setNombrePerfil(String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
   
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
   
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
}
