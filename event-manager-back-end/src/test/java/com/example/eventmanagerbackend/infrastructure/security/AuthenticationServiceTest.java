package com.example.eventmanagerbackend.infrastructure.security;

import com.example.eventmanagerbackend.domain.dtos.UserLoginRequestDTO;
import com.example.eventmanagerbackend.infrastructure.exceptions.TooManyLoginAttemptsException;
import com.example.eventmanagerbackend.infrastructure.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final LoginAttemptLimiter limiter = mock(LoginAttemptLimiter.class);
    private final AuthenticationService service =
            new AuthenticationService(jwtService, authenticationManager, limiter);

    private final UserLoginRequestDTO login = new UserLoginRequestDTO("ana", "secret");

    @Test
    void blockedThrowsWithoutCheckingCredentials() {
        when(limiter.tryAcquire("ana")).thenReturn(false);

        assertThrows(TooManyLoginAttemptsException.class, () -> service.authenticate(login));
        verifyNoInteractions(authenticationManager);
        verify(limiter, never()).reset(any());
    }

    @Test
    void failurePropagates() {
        when(limiter.tryAcquire("ana")).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(BadCredentialsException.class, () -> service.authenticate(login));
        verify(limiter, never()).reset(any());
    }

    @Test
    void successResetsAndReturnsToken() {
        Authentication authentication = mock(Authentication.class);
        when(limiter.tryAcquire("ana")).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt");

        assertEquals("jwt", service.authenticate(login));
        verify(limiter).reset("ana");
    }
}
