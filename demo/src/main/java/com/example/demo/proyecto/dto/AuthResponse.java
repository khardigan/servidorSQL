package com.example.demo.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// DTO de salida que devuelve el token y los datos del usuario al loguearse.
public class AuthResponse {
    // este el token jwt que se le envia al cliente
    private String token;
    private String nombre;
    private String email;
    private String rol;
    private Long id;

}
