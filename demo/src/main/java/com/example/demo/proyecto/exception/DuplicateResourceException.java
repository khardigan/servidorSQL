package com.example.demo.proyecto.exception;

/**
 * Excepción obsoleta. Se utilizaba para manejar recursos duplicados,
 * pero ha sido reemplazada por {@link RecursoDuplicadoException} para mantener
 * la nomenclatura en español.
 */
@Deprecated
public class DuplicateResourceException extends RecursoDuplicadoException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
