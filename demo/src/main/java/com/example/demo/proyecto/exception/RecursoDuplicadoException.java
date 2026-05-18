package com.example.demo.proyecto.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta crear o insertar
 * un recurso que ya existe en la base de datos (por ejemplo, registrar un usuario
 * con un email que ya está en uso).
 */
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
