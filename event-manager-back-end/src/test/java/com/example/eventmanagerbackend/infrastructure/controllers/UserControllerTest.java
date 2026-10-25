package com.example.eventmanagerbackend.infrastructure.controllers;

import com.example.eventmanagerbackend.domain.dtos.UserRequestDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.security.AuthenticationService;
import com.example.eventmanagerbackend.infrastructure.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final UserController controller = new UserController(userService, mock(AuthenticationService.class));
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerResponseContainsNoPassword() throws Exception {
        UserRequestDTO request = new UserRequestDTO("ana", "ana@gmail.com", "secret123");
        User user = new User();
        user.setId(1L);
        user.setUserName("ana");
        user.setEmail("ana@gmail.com");
        user.setPassword("$2a$10$hash");
        when(userService.createUser(request)).thenReturn(user);

        String json = objectMapper.writeValueAsString(controller.register(request).getBody());

        assertFalse(json.contains("password"));
        assertFalse(json.contains("$2a$10$hash"));
        assertTrue(json.contains("ana@gmail.com"));
    }

    @Test
    void userEntityNeverSerializesPassword() throws Exception {
        User user = new User();
        user.setPassword("$2a$10$hash");

        assertFalse(objectMapper.writeValueAsString(user).contains("$2a$10$hash"));
    }
}
