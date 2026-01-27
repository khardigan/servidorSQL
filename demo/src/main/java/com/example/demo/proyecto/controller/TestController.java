package com.example.demo.proyecto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // Ruta pública, accesible sin login
    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hola desde ruta pública!";
    }

    // Ruta privada, requiere login
    @GetMapping("/private/hello")
    public String privateHello() {
        return "Hola desde ruta privada!";
    }
}
