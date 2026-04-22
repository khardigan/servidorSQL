package com.example.demo.proyecto.exception;

// Excepción que se lanza cuando se intenta crear un recurso que ya existe.
@Deprecated
public class DuplicateResourceException extends RecursoDuplicadoException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
