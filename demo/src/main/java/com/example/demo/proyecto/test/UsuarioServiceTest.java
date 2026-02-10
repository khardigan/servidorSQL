package com.example.demo.proyecto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.proyecto.model.Usuario;
import com.example.demo.proyecto.repository.repositoryLista;
import com.example.demo.proyecto.repository.repositoryProducto;
import com.example.demo.proyecto.repository.repositoryUsuario;

@ExtendWith(MockitoExtension.class)
class ServiceAuthenTest {

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
    //hola

    @Test
    void guardarUsuario_ok_cuandoNoExiste() {
        // ARRANGE
        Usuario usuario = new Usuario(
                "pepe",
                "pepe@email.com",
                "1234",
                "USER",
                LocalDate.now()
        );

        when(repoUsuario.findAll()).thenReturn(List.of());
        when(repoUsuario.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = serviceAuthen.guardarUsuario(usuario);

        // ASSERT
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
            com.example.demo.proyecto.exception.RecursoDuplicadoException.class,
            () -> serviceAuthen.guardarUsuario(usuario)
        );

        verify(repoUsuario, never()).save(any());
    }
}
