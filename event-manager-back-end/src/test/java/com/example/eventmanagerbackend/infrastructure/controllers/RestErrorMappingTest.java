package com.example.eventmanagerbackend.infrastructure.controllers;

import com.example.eventmanagerbackend.domain.dtos.GoogleTokenDTO;
import com.example.eventmanagerbackend.domain.dtos.UserLoginRequestDTO;
import com.example.eventmanagerbackend.domain.dtos.UserRequestDTO;
import com.example.eventmanagerbackend.infrastructure.exceptions.EmployeeTypeNotFoundException;
import com.example.eventmanagerbackend.infrastructure.exceptions.InvalidGoogleTokenException;
import com.example.eventmanagerbackend.infrastructure.exceptions.TooManyLoginAttemptsException;
import com.example.eventmanagerbackend.infrastructure.exceptions.UserNameExistsException;
import com.example.eventmanagerbackend.infrastructure.security.AuthenticationService;
import com.example.eventmanagerbackend.infrastructure.services.AuthService;
import com.example.eventmanagerbackend.infrastructure.services.EmployeeTypeService;
import com.example.eventmanagerbackend.infrastructure.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestErrorMappingTest {

    private final UserService userService = mock(UserService.class);
    private final AuthenticationService authenticationService = mock(AuthenticationService.class);
    private final AuthService authService = mock(AuthService.class);
    private final EmployeeTypeService employeeTypeService = mock(EmployeeTypeService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(
                    new UserController(userService, authenticationService),
                    new AuthController(authService),
                    new EmployeeTypeController(employeeTypeService))
            .setControllerAdvice(new RestExceptionHandler())
            .build();

    private ResultActions postJson(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @Test
    void loginBlockedByLimiterReturns429() throws Exception {
        when(authenticationService.authenticate(any())).thenThrow(new TooManyLoginAttemptsException());

        postJson("/api/user/login", new UserLoginRequestDTO("ana", "secret123"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Muitas tentativas de login. Tente novamente mais tarde."));
    }

    @Test
    void loginWithBlankUsernameReturns400() throws Exception {
        postJson("/api/user/login", new UserLoginRequestDTO(" ", "secret123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O username é obrigatório"));
        verifyNoInteractions(authenticationService);
    }

    @Test
    void loginWithUsernameOver50CharsReturns400() throws Exception {
        postJson("/api/user/login", new UserLoginRequestDTO("a".repeat(51), "secret123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O username deve ter no máximo 50 caracteres"));
        verifyNoInteractions(authenticationService);
    }

    @Test
    void registerWithInvalidEmailReturns400() throws Exception {
        postJson("/api/user/register", new UserRequestDTO("ana", "not-an-email", "secret123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email inválido"));
        verifyNoInteractions(userService);
    }

    @Test
    void registerWithExistingUsernameReturns409() throws Exception {
        when(userService.createUser(any())).thenThrow(new UserNameExistsException());

        postJson("/api/user/register", new UserRequestDTO("ana", "ana@gmail.com", "secret123"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Usuário já cadastrado"));
    }

    @Test
    void googleLoginWithInvalidTokenReturns401() throws Exception {
        when(authService.authenticateOrRegisterGoogleUser(any())).thenThrow(new InvalidGoogleTokenException());

        postJson("/api/auth/google", new GoogleTokenDTO("bad-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void googleLoginWithBlankTokenReturns400() throws Exception {
        postJson("/api/auth/google", new GoogleTokenDTO(" "))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(authService);
    }

    @Test
    void googleLoginWithMissingTokenReturns400() throws Exception {
        postJson("/api/auth/google", new GoogleTokenDTO(null))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(authService);
    }

    @Test
    void deletingUnknownEmployeeTypeReturns404() throws Exception {
        doThrow(new EmployeeTypeNotFoundException()).when(employeeTypeService).deleteEmployeeType(99L);

        mockMvc.perform(delete("/api/type/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee type not found"));
    }
}
