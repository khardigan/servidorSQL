package com.example.demo.proyecto.exception;

// Excepción que se lanza cuando se intenta crear un recurso que ya existe.
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
