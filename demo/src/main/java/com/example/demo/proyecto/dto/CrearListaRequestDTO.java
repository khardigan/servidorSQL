package com.example.demo.proyecto.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;

public class CrearListaRequestDTO {

    @NotNull(message = "El dueño de la lista es obligatorio")
    private Long usuarioDuenoId;

    private List<Long> usuariosCompartida;   // IDs de usuarios con los que se comparte
    private List<Long> productosEnLista;      // IDs de productos en la lista

    // Getters y Setters
    public Long getUsuarioDuenoId() { return usuarioDuenoId; }
    public void setUsuarioDuenoId(Long usuarioDuenoId) { this.usuarioDuenoId = usuarioDuenoId; }
    public List<Long> getUsuariosCompartida() { return usuariosCompartida; }
    public void setUsuariosCompartida(List<Long> usuariosCompartida) { this.usuariosCompartida = usuariosCompartida; }
    public List<Long> getProductosEnLista() { return productosEnLista; }
    public void setProductosEnLista(List<Long> productosEnLista) { this.productosEnLista = productosEnLista; }
}
