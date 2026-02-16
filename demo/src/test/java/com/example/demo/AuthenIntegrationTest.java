package com.example.demo;

import com.example.demo.proyecto.dto.CrearProductoDTO;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthenIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private serviceJWT jwtService;

        @Autowired
        private repositoryUsuario repoUsuario;
        @Autowired
        private repositoryProducto repoProducto;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void registrarUsuario_integracion_ok() throws Exception {
                CrearUsuarioRequestDTO dto = new CrearUsuarioRequestDTO();
                dto.setNombre("Pepe Perez");
                dto.setEmail("pepe@email.com");
                dto.setContraseña("123456");
                dto.setRol("USER");

                mockMvc.perform(post("/usuarios/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated());

                // 3. Verificación en la base de datos
                boolean existe = repoUsuario.findAll().stream()
                                .anyMatch(u -> u.getNombre().equals("Pepe Perez"));

                assertTrue(existe);
        }

        @Test
        public void registrarProducto_integracion_ok() throws Exception {
                // Configuramos el "doble" para que no valide nada y devuelva un usuario
                // inventado
                when(jwtService.esTokenValido(anyString())).thenReturn(true);
                when(jwtService.obtenerSubject(anyString())).thenReturn("usuario_admin");

                CrearProductoDTO producto = new CrearProductoDTO();
                producto.setNombre("Producto de prueba");
                producto.setDescripcion("Descripción válida de prueba");
                producto.setPrecio(100.0);
                producto.setCantidad(10);

                mockMvc.perform(post("/productos/pending")
                                .header("Authorization", "Bearer token.inventado") // El token da igual porque está
                                                                                   // mockeado
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(producto)))
                                .andExpect(status().isCreated());

                // Verificación
                boolean existe = repoProducto.findAll().stream()
                                .anyMatch(p -> p.getNombre().equals("Producto de prueba"));

                assertTrue(existe);
        }

        // -------------------------------------------------------------------
        // NUEVAS PRUEBAS DE INTEGRACIÓN - VARIADAS (Contexto y Endpoints)
        // -------------------------------------------------------------------

        @Test
        public void contextLoads() {
                // Verifica que el contexto de Spring se cargue correctamente
                // y que los beans principales estén presentes.
                org.junit.jupiter.api.Assertions.assertNotNull(mockMvc);
                org.junit.jupiter.api.Assertions.assertNotNull(repoUsuario);
        }

        @Test
        public void listarUsuarios_integracion_debeRetornarLista() throws Exception {
                // Simulamos un token válido para acceder al endpoint protegido
                // Nota: En AuthenIntegrationTest ya está @AutoConfigureMockMvc(addFilters =
                // false)
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/usuarios")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        public void login_integracion_fallido_credencialesMalas() throws Exception {
                // Prueba de integración del flujo de login fallido
                String jsonBody = "{\"nombre\":\"noexiste\", \"password\":\"mal\"}";

                mockMvc.perform(post("/usuarios/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                                .andExpect(status().isUnauthorized());
        }
}