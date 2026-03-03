package com.example.demo.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    // este el token jwt que se le envia al cliente
    private String token;
    private String nombre;
    private String rol;
    private Long id;

}
