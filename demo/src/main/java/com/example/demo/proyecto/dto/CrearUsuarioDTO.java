package com.example.demo.proyecto.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

public class CrearUsuarioDTO {

    @NotBlank(message = "El nombre de usuario no puede ser nulo")
    private String nombre;

    @NotBlank(message = "El email no puede ser nulo")
    private String email;

    @NotBlank(message = "La contraseña no puede ser nula")
    private String contraseña;

    @NotBlank(message = "El rol no puede ser nulo")
    private String rol;

    @PastOrPresent(message = "La fecha de registro no puede ser futura")
    private LocalDate fechaRegistro; 

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
