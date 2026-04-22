package com.example.demo.proyecto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.example.demo.proyecto.dto.CrearProductoPropioDTO;
import com.example.demo.proyecto.dto.ProductoPropioDTO;
import com.example.demo.proyecto.service.serviceProductoPropio;

@RestController
@RequestMapping("/productos-propios")
public class controllerProductoPropio {
    private final serviceProductoPropio serviceProductoPropio;

    public controllerProductoPropio(serviceProductoPropio serviceProductoPropio) {
        this.serviceProductoPropio = serviceProductoPropio;
    }

    // Te da los productos propios del usuario. (Tiene que recibir el Token y el ID)
    @GetMapping("/{usuarioId}")
    public List<ProductoPropioDTO> obtenerProductosPropios(@PathVariable Long usuarioId) {
        return serviceProductoPropio.obtenerLista(usuarioId);
    }

    // Crea un producto propio. (Tiene que recibir el Token y el ID)
    @PostMapping("/{usuarioId}")
    public ProductoPropioDTO crearProductoPropio(@PathVariable Long usuarioId,
            @RequestBody CrearProductoPropioDTO dto) {
        return serviceProductoPropio.crearItem(usuarioId, dto);
    }

    // Actualiza un producto propio. (Tiene que recibir el Token y el ID)
    @PutMapping("/{id}")
    public ProductoPropioDTO actualizarProductoPropio(@PathVariable Long id,
            @RequestBody CrearProductoPropioDTO dto) {
        return serviceProductoPropio.actualizarItem(id, dto);
    }

    // Elimina un producto propio. (Tiene que recibir el Token y el ID)
    @DeleteMapping("/{id}")
    public void eliminarProductoPropio(@PathVariable Long id) {
        serviceProductoPropio.eliminarItem(id);
    }

}
