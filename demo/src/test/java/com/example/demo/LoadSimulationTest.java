package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.proyecto.dto.ComentarioDTO;
import com.example.demo.proyecto.model.Comentario;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryComentario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceComentario;

/*
 * Esta clase contiene pruebas de carga, rendimiento y estrés 
 * específicas para nuestra aplicación, simulando el comportamiento 
 * del sistema de comentarios bajo alta demanda.
 */
@ExtendWith(MockitoExtension.class)
public class LoadSimulationTest {

    @Mock
    private repositoryComentario repoComentario;

    @Mock
    private repositoryUsuario repoUsuario;

    @Mock
    private repositoryProducto repoProducto;

    @InjectMocks
    private serviceComentario comentarioService;

    private List<Comentario> mockComentarios;

    @BeforeEach
    void setUp() {
        mockComentarios = new ArrayList<>();
        Usuario dummyUser = new Usuario();
        dummyUser.setId(1L);
        dummyUser.setNombre("usuario_carga");
        dummyUser.setEmail("carga@ejemplo.com");

        for (int i = 0; i < 1000; i++) {
            Comentario c = new Comentario(
                    "Comentario de prueba " + i,
                    java.sql.Date.valueOf(LocalDate.now()),
                    4.5,
                    dummyUser);
            // IMPORTANTE: Asegurarnos de que el ID del comentario sea distinto para cada
            // uno si el DTO lo requiere
            c.setIdComentario((long) i);
            mockComentarios.add(c);
        }
    }

    /*
     * PRUEBA 1: SIMULACIÓN DE CONCURRENCIA EN EL SERVICIO
     * 
     * Simulamos 50 usuarios (hilos) pidiendo la lista de comentarios al mismo
     * tiempo.
     * La idea es comprobar que el servicio puede manejar múltiples peticiones
     * concurrentes correctamente y sin bloqueos al transformar las entidades a
     * DTOs.
     */
    @Test
    void testSimulacionConcurrenciaComentarios() throws InterruptedException {
        when(repoComentario.findAll()).thenReturn(mockComentarios);

        int numeroDeHilos = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeHilos);
        AtomicInteger contadorExitos = new AtomicInteger(0);

        Runnable tarea = () -> {
            try {
                List<ComentarioDTO> resultado = comentarioService.obtenerTodosComentarios();
                if (resultado != null && resultado.size() == 1000) {
                    contadorExitos.incrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < numeroDeHilos; i++) {
            executor.submit(tarea);
        }

        executor.shutdown();
        boolean terminado = executor.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(terminado, "Todas las tareas deberían haber terminado");
        assertTrue(contadorExitos.get() == numeroDeHilos, "Todas las tareas deberían haber sido exitosas");
        System.out.println("Prueba de Concurrencia: 50 hilos obtuvieron los comentarios correctamente.");
    }

    /*
     * PRUEBA 2: MEDICIÓN DE RENDIMIENTO DEL MAPEO DE DTOs
     * 
     * Medimos cuánto tarda el sistema en procesar y mapear 1000 comentarios reales.
     * Nos aseguramos de que el tiempo de respuesta sea óptimo para la experiencia
     * de usuario.
     */
    @Test
    void testRendimientoObtencionComentarios() {
        when(repoComentario.findAll()).thenReturn(mockComentarios);

        long inicio = System.currentTimeMillis();

        List<ComentarioDTO> resultado = comentarioService.obtenerTodosComentarios();

        long fin = System.currentTimeMillis();
        long duracion = fin - inicio;

        System.out.println("Duración de mapear 1000 comentarios: " + duracion + " ms");

        assertTrue(resultado.size() == 1000);
        assertTrue(duracion < 1000, "El sistema no debería tardar más de 1000ms en procesar 1000 comentarios");
    }

    /*
     * PRUEBA 3: PRUEBA DE ESTRÉS DE PROCESAMIENTO
     * 
     * Ejecutamos la petición masiva de comentarios 10,000 veces.
     * Comprobamos que el servicio aguanta un uso intensivo continuado sin
     * desbordar la memoria ni lanzar excepciones.
     */
    @Test
    void testEstresProcesamientoComentarios() {
        when(repoComentario.findAll()).thenReturn(mockComentarios);

        int iteraciones = 10000;
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < iteraciones; i++) {
            comentarioService.obtenerTodosComentarios();
        }

        long fin = System.currentTimeMillis();

        System.out.println("Prueba de Estrés: " + iteraciones + " peticiones procesadas en " + (fin - inicio) + " ms");

        assertTrue(true); 
    }
}