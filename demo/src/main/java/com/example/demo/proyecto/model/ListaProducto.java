package com.example.demo.proyecto.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lista_productos")
@Data
public class ListaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lista_id")
    @JsonBackReference(value = "lista-producto-lista")
    private Lista lista;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JsonBackReference(value = "lista-producto-producto")
    private Producto producto;

    private boolean comprado;
}
