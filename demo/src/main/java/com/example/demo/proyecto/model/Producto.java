package com.example.demo.proyecto.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "producto")
@Data

public class Producto {
    // Tiene nombre, descripcion,precio,cantidad, usuario que lo creo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Length(min = 3, max = 100, message = "El minimo de caracteres es 3 y el maximo es 100")
    private String nombre;

    @NotBlank(message = "La descripcion no puede estar vacia")
    @Length(min = 5, max = 300, message = "El minimo de caracteres es 5 y el maximo es 300")
    private String descripcion;
    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser positivo")
    private Double precio;
    @Range(min = 1, max = 1000, message = "Debes comprar minimo 1 y maximo 100 por cada compra")
    private int cantidad;
    private boolean confirmado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuarioRegistrador;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaProducto> listas;

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }
}
