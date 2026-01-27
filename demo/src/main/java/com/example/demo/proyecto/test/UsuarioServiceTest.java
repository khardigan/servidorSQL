package com.example.demo.proyecto.test;

public class UsuarioServiceTest { //caso de prueba unitario 
    @Mock
    private repositoryUsuario repositoryUsuario;

    @InjectMocks
    private serviceAuthen serviceAuthen;

    @Test
    void crearUsuario_ok() {
        User user = new User();
        user.setUsername("pepe");

        when(userRepository.save(any())).thenReturn(user);

        User result = userService.crear(user);

        assertEquals("pepe", result.getUsername());
    }

}