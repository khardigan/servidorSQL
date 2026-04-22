package com.example.demo.proyecto.dto;

// DTO de entrada para unirse a una lista compartida. Tiene que recibir el código de la lista.
public class UnirseListaRequestDTO {
    private String codigo;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
