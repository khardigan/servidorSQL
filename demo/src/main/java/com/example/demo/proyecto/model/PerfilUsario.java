package com.example.demo.proyecto.model;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "nombrePerfil"  
)
@Entity
@Table(name = "perfil_usuario")
@Data
public class PerfilUsario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private int id;

    @Column(name="nombre_perfil", unique=true)
    private String nombrePerfil;

    
    @Column(name="descripcion", length=200)
    @Length(max=200, message="El maximo de caracteres es 200")
    private String descripcion;

    @Column(name="subtitulo")
    private String subtitulo;

    @Column(name="fecha_nacimiento")
    private String fechaNacimiento;

    @Column(name="edad")
    private String edad;

    @Column(name="residencia")
    private String residencia;

    @Column(name="email")
    private String email;

    @Column(name="telefono")
    private String telefono;

    @OneToOne
    @JoinColumn(name="usuario_id")
    @JsonBackReference
    private Usuario usuario;

}