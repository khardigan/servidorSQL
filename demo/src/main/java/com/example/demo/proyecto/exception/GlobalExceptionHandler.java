package com.example.demo.proyecto.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Interceptor global de excepciones para todos los controladores.
 * Captura excepciones específicas lanzadas en cualquier punto de la aplicación
 * y devuelve una respuesta HTTP estandarizada en formato JSON, evitando 
 * que el servidor devuelva errores genéricos o trazas de la pila (stacktraces).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura la excepción RecursoDuplicadoException y devuelve un HTTP 409 (Conflict).
     */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarRecursoDuplicado(RecursoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
    }

}
