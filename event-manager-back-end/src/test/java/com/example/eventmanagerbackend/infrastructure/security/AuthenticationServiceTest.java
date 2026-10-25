package com.example.eventmanagerbackend.infrastructure.security;

import com.example.eventmanagerbackend.domain.dtos.UserLoginRequestDTO;
import com.example.eventmanagerbackend.infrastructure.exceptions.TooManyLoginAttemptsException;
import com.example.eventmanagerbackend.infrastructure.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AuthenticationService service =
            new AuthenticationService(jwtService, authenticationManager, new LoginAttemptLimiter());

    private final UserLoginRequestDTO wrong = new UserLoginRequestDTO("ana", "wrong");
    private final UserLoginRequestDTO right = new UserLoginRequestDTO("ana", "right");

    @Test
    void blocksAfterMaxFailuresWithoutCheckingCredentials() {
        failWrongPassword();
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES; i++) {
            assertThrows(BadCredentialsException.class, () -> service.authenticate(wrong));
        }

        assertThrows(TooManyLoginAttemptsException.class, () -> service.authenticate(right));
        verify(authenticationManager, times(LoginAttemptLimiter.MAX_FAILURES)).authenticate(any());
    }

    @Test
    void successResetsFailures() {
        failWrongPassword();
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("jwt");
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES - 1; i++) {
            assertThrows(BadCredentialsException.class, () -> service.authenticate(wrong));
        }

        assertEquals("jwt", service.authenticate(right));

        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES - 1; i++) {
            assertThrows(BadCredentialsException.class, () -> service.authenticate(wrong));
        }
        assertEquals("jwt", service.authenticate(right));
    }

    private void failWrongPassword() {
        when(authenticationManager.authenticate(any())).thenAnswer(inv -> {
            UsernamePasswordAuthenticationToken token = inv.getArgument(0);
            if ("right".equals(token.getCredentials())) {
                return token;
            }
            throw new BadCredentialsException("bad");
        });
    }
}
