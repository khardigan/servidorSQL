package com.example.demo.proyecto.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.proyecto.service.EmailService;

@RestController
@RequestMapping("/contacto")
public class controllerContacto {

    private final EmailService emailService;

    public controllerContacto(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public ResponseEntity<?> obtenerEmail() {
        return ResponseEntity.ok(Map.of("email", emailService.getEmailOficial()));
    }

    @PostMapping
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, String> request) {
        String nombre = request.get("nombre");
        String correo = request.get("correo");
        String tema = request.get("tema");
        String mensaje = request.get("mensaje");

        if (nombre == null || correo == null || tema == null || mensaje == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Todos los campos son obligatorios"));
        }

        try {
            emailService.enviarEmailContacto(nombre, correo, tema, mensaje);
            return ResponseEntity.ok(Map.of("mensaje", "Mensaje enviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al enviar el mensaje: " + e.getMessage()));
        }
    }
}
