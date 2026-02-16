package com.example.demo;

// Importamos lo necesario para hacer pruebas con JUnit
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/*
 * Esta clase contiene pruebas de carga, rendimiento y estrés.
    No usamos herramientas externas, sino que simulamos nosotros
    mismos situaciones donde el sistema trabaja mucho o con varios usuarios a la vez.
 */
public class LoadSimulationTest {

    /*
     * PRUEBA 1: SIMULACIÓN DE CONCURRENCIA
     * 
     * Aquí simulamos 50 usuarios (hilos) ejecutando algo al mismo tiempo.
     * La idea es comprobar que el sistema puede manejar varias tareas simultáneas
     * sin fallar ni quedarse bloqueado.
     */
    @Test
    void testSimulacionConcurrencia() throws InterruptedException {
        // Número de hilos (usuarios simulados)
        int numeroDeHilos = 50;
        // Creamos un "pool" de hilos con 50 trabajadores
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeHilos);
        // Contador seguro para entornos concurrentes (evita errores entre hilos)
        AtomicInteger contadorExitos = new AtomicInteger(0);

        // Definimos la tarea que ejecutará cada hilo
        Runnable tarea = () -> {
            try {
                // Simulamos una pequeña operación (como acceder a base de datos) y si termina
                // correctamente, aumentamos el contador
                Thread.sleep(10);
                contadorExitos.incrementAndGet();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Enviamos la tarea 50 veces (una por cada hilo)
        for (int i = 0; i < numeroDeHilos; i++) {
            executor.submit(tarea);
        }

        // Indicamos que no se enviarán más tareas
        executor.shutdown();

        // Esperamos máximo 5 segundos a que todos los hilos terminen
        boolean terminado = executor.awaitTermination(5, TimeUnit.SECONDS);

        // Comprobamos que todos terminaron
        assertTrue(terminado, "Todas las tareas deberían haber terminado");

        // Comprobamos que las 50 tareas se ejecutaron correctamente
        assertTrue(contadorExitos.get() == numeroDeHilos,
                "Todas las tareas deberían haber sido exitosas");

        System.out.println("Prueba de Concurrencia: " + numeroDeHilos + " hilos ejecutados correctamente.");
    }

    /*
     * PRUEBA 2: MEDICIÓN DE RENDIMIENTO
     * 
     * Aquí medimos cuánto tarda en ejecutarse una operación.
     * La idea es comprobar que no supera un tiempo límite aceptable.
     */
    @Test
    void testRendimientoOperacion() {

        // Guardamos el tiempo antes de empezar
        long inicio = System.currentTimeMillis();

        // Ejecutamos la operación que queremos medir
        operacionPesadaSimulada();

        // Guardamos el tiempo al terminar
        long fin = System.currentTimeMillis();

        // Calculamos cuánto ha tardado
        long duracion = fin - inicio;

        System.out.println("Duración operación: " + duracion + " ms");

        // Verificamos que no tarde más de 500 milisegundos
        assertTrue(duracion < 500,
                "La operación no debería tardar más de 500ms");
    }

    /*
     * Método que simula una operación pesada.
     * Aquí simplemente hacemos que el sistema espere 100ms
     * como si estuviera procesando algo complejo.
     */
    private void operacionPesadaSimulada() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * PRUEBA 3: PRUEBA DE ESTRÉS
     * 
     * Ejecutamos una operación sencilla 10.000 veces seguidas.
     * La idea es comprobar que el sistema aguanta muchas ejecuciones
     * sin lanzar errores ni romperse.
     */
    @Test
    void testEstresBucle() {

        int iteraciones = 10000;

        long inicio = System.currentTimeMillis();

        // Repetimos muchas veces una operación matemática simple
        for (int i = 0; i < iteraciones; i++) {
            Math.sqrt(i * 1234.56);
        }

        long fin = System.currentTimeMillis();

        System.out.println("Prueba de Estrés: " + iteraciones +
                " iteraciones en " + (fin - inicio) + " ms");

        // Si el código llega aquí sin errores, la prueba pasa
        assertTrue(true);
    }
}