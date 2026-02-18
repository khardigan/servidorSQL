package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.proyecto.exception.RecursoDuplicadoException;
import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;
import com.example.demo.proyecto.service.serviceJWT;
import com.example.demo.proyecto.service.serviceAuthen;

@ExtendWith(MockitoExtension.class)

public class ServiceAuthenTest {

    @Mock
    private repositoryUsuario repoUsuario;
    @Mock
    private repositoryProducto repoProducto;
    @Mock
    private repositoryLista repoLista;
    @Mock
    private serviceJWT jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private serviceAuthen serviceAuthen;

    @Test
    void guardarUsuario_ok_cuandoNoExiste() {
        Usuario usuario = new Usuario(
                "pepe",
                "pepe@email.com",
                "1234",
                "USER",
                LocalDate.now());

        when(repoUsuario.findAll()).thenReturn(List.of());
        when(repoUsuario.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = serviceAuthen.guardarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("pepe", resultado.getNombre());
        assertNotNull(resultado.getPerfilUsuario());
        assertNotNull(resultado.getListaProductosSubidos());

        verify(repoUsuario).save(usuario);
    }

    @Test
    void guardarUsuario_lanzaExcepcion_siUsuarioExiste() {
        Usuario usuario = new Usuario();
        usuario.setNombre("admin");

        Usuario existente = new Usuario();
        existente.setNombre("admin");

        when(repoUsuario.findAll()).thenReturn(List.of(existente));

        assertThrows(
                RecursoDuplicadoException.class,
                () -> serviceAuthen.guardarUsuario(usuario));

        verify(repoUsuario, never()).save(any());
    }

    @Test
    public void guardarUsuario_debeCifrarPassword() {
        Usuario usuario = new Usuario("ana", "ana@mail.com", "password123", "USER", LocalDate.now());
        when(repoUsuario.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode(anyString())).thenReturn("bcrypted_password");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        when(repoUsuario.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = serviceAuthen.guardarUsuario(usuario);

        assertNotNull(resultado.getContraseña());
        assertTrue(passwordEncoder.matches("password123", resultado.getContraseña()));
        verify(passwordEncoder).encode("password123");
    }

    // -------------------------------------------------------------------
    // NUEVAS PRUEBAS - VARIADAS (Login y Actualización)
    // -------------------------------------------------------------------

    @Test
    void login_exitoso_devuelveToken() {
        Usuario usuario = new Usuario();
        usuario.setNombre("juan");
        usuario.setContraseña("encodedPassword");
        usuario.setRol("USER");
        usuario.setId(1L);

        when(repoUsuario.findAll()).thenReturn(List.of(usuario));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generarToken("juan", "USER", 1L)).thenReturn("token_jwt");

        com.example.demo.proyecto.dto.AuthResponse response = serviceAuthen.login("juan", "password");

        assertNotNull(response);
        assertEquals("token_jwt", response.getToken());
        assertEquals("juan", response.getNombre());
    }

    @Test
    void login_fallido_siPasswordIncorrecta() {
        Usuario usuario = new Usuario();
        usuario.setNombre("juan");
        usuario.setContraseña("encodedPassword");

        when(repoUsuario.findAll()).thenReturn(List.of(usuario));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        com.example.demo.proyecto.dto.AuthResponse response = serviceAuthen.login("juan", "wrongpass");

        assertNull(response);
    }

    @Test
    void actualizarUsuario_debeGuardarCambios() {
        Usuario datosNuevos = new Usuario();
        datosNuevos.setNombre("Juan Modificado");

        when(repoUsuario.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = serviceAuthen.actualizarUsuario(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Modificado", resultado.getNombre());
        verify(repoUsuario).save(datosNuevos);
        
    }
}
