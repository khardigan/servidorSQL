package com.example.demo.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos_propios")
public class ProductoPropio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private Double precioObjetivo;

    @Column(length = 500)
    private String notas;

    /* id lista, puede estar vacia */
    @ManyToOne
    @JoinColumn(name = "lista_id", nullable = true)
    private Lista lista;

    private String supermercado;

    private Integer cantidad = 1;

    private Boolean comprado = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public ProductoPropio() {
    }

    public ProductoPropio(Usuario usuario, String nombre, Double precioObjetivo, String notas) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.precioObjetivo = precioObjetivo;
        this.notas = notas;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Lista getLista() {
        return lista;
    }

    public void setLista(Lista lista) {
        this.lista = lista;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getComprado() {
        return comprado;
    }

    public void setComprado(Boolean comprado) {
        this.comprado = comprado;
    }
}
