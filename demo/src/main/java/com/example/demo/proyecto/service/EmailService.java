package com.example.demo.proyecto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacion(String to, String token, String nombre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("LinkedListOficial@gmail.com");
        message.setTo(to);
        message.setSubject("Recuperación de Contraseña - LinkedList");

        // La URL que llevará al frontend para resetear password
        String url = "http://localhost:4200/reset-password?token=" + token + "&nombre=" + nombre;

        String body = "Hola " + nombre + ",\n\n"
                + "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:\n\n"
                + url + "\n\n"
                + "Este enlace caducará pronto.\n\n"
                + "Si no has solicitado este cambio, ignora este correo.\n"
                + "Saludos,\nEl equipo de LinkedList.";

        message.setText(body);
        mailSender.send(message);
    }

    public void enviarEmailVerificacion(String to, String token, String nombre) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("LinkedListOficial@gmail.com");
        message.setTo(to);
        message.setSubject("Verificación de Cuenta - LinkedList");

        // La URL que llevará al frontend para validar la cuenta
        String url = "http://localhost:4200/verificar-cuenta?token=" + token;

        String body = "Hola " + nombre + ",\n\n"
                + "Gracias por registrarte en LinkedList. Para activar tu cuenta, haz clic en el siguiente enlace:\n\n"
                + url + "\n\n"
                + "Si no has solicitado este registro, ignora este correo.\n"
                + "Saludos,\nEl equipo de LinkedList.";

        message.setText(body);
        mailSender.send(message);
    }

    public void enviarEmailContacto(String nombre, String de, String tema, String mensajeContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("LinkedListOficial@gmail.com");
        message.setTo("LinkedListOficial@gmail.com"); // Se envía al correo oficial
        message.setReplyTo(de); // Para poder responder al usuario
        message.setSubject("NUEVO MENSAJE DE CONTACTO: " + tema);

        String body = "Has recibido un nuevo mensaje desde el formulario de contacto:\n\n"
                + "Nombre: " + nombre + "\n"
                + "Correo: " + de + "\n"
                + "Tema: " + tema + "\n\n"
                + "Mensaje:\n" + mensajeContent + "\n\n"
                + "--- FIN DEL MENSAJE ---";

        message.setText(body);
        mailSender.send(message);
    }
}
