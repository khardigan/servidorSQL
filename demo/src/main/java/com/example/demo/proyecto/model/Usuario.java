package com.example.demo.proyecto.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario no puede ser nulo")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
    @Column(unique = true)
    private String nombre;
    @NotBlank(message = "El email no puede ser nulo")
    @Size(min = 5, max = 255, message = "El email debe tener entre 5 y 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña no puede ser nula")
    @Size(min = 4, max = 255, message = "La contraseña debe tener entre 4 y 255 caracteres")
    private String contraseña;

    @NotBlank(message = "El rol no puede ser nulo")
    private String rol;

    @PastOrPresent(message = "La fecha de registro no puede ser futura")
    private LocalDate fechaRegistro;

    private Boolean activo = true;

    // Campos para recuperación de contraseña
    private String resetToken;
    private LocalDateTime resetTokenExpiration;

    // Campos para verificación de email
    private String verificationToken;

 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usuario-comentarios")
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioRegistrador", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usuario-productos")
    private List<Producto> listaProductosSubidos;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usuario-perfil")
    private PerfilUsario perfilUsuario;

    @OneToMany(mappedBy = "usuarioDueno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usuario-listas")
    private List<Lista> listasCreadas;

    @ManyToMany(mappedBy = "usuariosCompartida")
    private List<Lista> listasCompartidas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoPropio> productosPropios;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String contraseña, String rol, LocalDate fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.listaProductosSubidos = null;
        this.perfilUsuario = null;
        this.listasCreadas = null;
        this.listasCompartidas = null;
    }
}
