package com.example.demo;

import com.example.demo.proyecto.dto.CrearProductoDTO;
import com.example.demo.proyecto.dto.CrearUsuarioRequestDTO;
import com.example.demo.proyecto.dto.ProductoDTO;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthenIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private serviceJWT jwtService;

        @MockBean
        private com.example.demo.proyecto.repository.repositoryLista repoLista;

        @Autowired
        private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

                when(jwtService.generarToken(anyString(), anyString(), any())).thenReturn("mocked-token");

                mockMvc.perform(post("/usuarios/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated());

                boolean existe = repoUsuario.findAll().stream()
                                .anyMatch(u -> u.getNombre().equals("Pepe Perez"));

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
                // Creamos un usuario válido en la base de datos con contraseña cifrada
                Usuario u = new Usuario();
                u.setNombre("usuario_valido");
                u.setContraseña(passwordEncoder.encode("123456"));
                u.setRol("USER");
                u.setEmail("usuario@correo.com");
                u.setActivo(true);
                repoUsuario.save(u);

                // Intentamos login con contraseña incorrecta
                String jsonBody = "{\"nombre\":\"usuario_valido\", \"password\":\"mal\"}";
                mockMvc.perform(post("/usuarios/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                                .andExpect(status().isUnauthorized());
        }

}