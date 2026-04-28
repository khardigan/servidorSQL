
package com.example.demo.proyecto.controller;

import com.example.demo.proyecto.dto.ComentarioDTO;
import com.example.demo.proyecto.dto.CrearComentarioDTO;
import com.example.demo.proyecto.service.serviceComentario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@CrossOrigin(origins = "http://localhost:4200") // Para que Angular no llore con CORS
public class controllerComentario {

    private final serviceComentario serviceComentario;

    public controllerComentario(serviceComentario serviceComentario) {
        this.serviceComentario = serviceComentario;
    }

    @GetMapping("/todos")
    public List<ComentarioDTO> obtenerTodos() {
        return serviceComentario.obtenerTodosComentarios();
    }

    @GetMapping("/mis-comentarios/{idUsuario}")
    public List<ComentarioDTO> obtenerMisComentarios(@PathVariable Long idUsuario) {
        return serviceComentario.obtenerMisComentarios(idUsuario);
    }

    @GetMapping("/producto/{idProducto}")
    public List<ComentarioDTO> obtenerPorProducto(@PathVariable Long idProducto) {
        return serviceComentario.obtenerComentariosPorProducto(idProducto);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearComentario(@RequestBody CrearComentarioDTO dto) {
        boolean creado = serviceComentario.agregarComentario(dto);
        if (creado)
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body("No se pudo crear el comentario");
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ComentarioDTO dto) {
        boolean ok = serviceComentario.actualizarComentario(id, dto.getContenido(), dto.getPuntuacion());
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean ok = serviceComentario.eliminarComentario(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
