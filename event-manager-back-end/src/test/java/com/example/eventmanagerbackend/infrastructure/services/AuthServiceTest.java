package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.GoogleTokenDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.InvalidGoogleTokenException;
import com.example.eventmanagerbackend.infrastructure.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
    private final AuthService authService = new AuthService(userRepository, passwordEncoder, jwtService, verifier);

    @BeforeEach
    void setUp() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenAnswer(inv -> "hash:" + inv.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt");
    }

    @Test
    void ssoUsersGetRandomPasswordThatDiffersPerUser() throws Exception {
        stubToken("t1", payload("ana@gmail.com", true, "Ana"));
        stubToken("t2", payload("bia@gmail.com", true, "Bia"));

        authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t1"));
        authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t2"));

        ArgumentCaptor<String> raw = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder, times(2)).encode(raw.capture());
        List<String> passwords = raw.getAllValues();
        assertNotEquals(passwords.get(0), passwords.get(1));
        passwords.forEach(p -> {
            assertNotEquals("GOOGLE_SSO_PASSWORD_PLACEHOLDER", p);
            assertTrue(p.length() >= 32);
        });
    }

    @Test
    void ssoUserNameCollisionGetsNumericSuffix() throws Exception {
        when(userRepository.findByUserName("joaosilva")).thenReturn(Optional.of(new User()));
        when(userRepository.findByUserName("joaosilva1")).thenReturn(Optional.of(new User()));
        stubToken("t", payload("joao.silva@gmail.com", true, "João Silva"));

        authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t"));

        assertEquals("joaosilva2", savedUser().getUserName());
    }

    @Test
    void ssoUserNameStartsWithLetter() throws Exception {
        stubToken("t", payload("123.abc@gmail.com", true, "Abc"));

        authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t"));

        assertEquals("abc", savedUser().getUserName());
    }

    @Test
    void nullNameClaimIsHandled() throws Exception {
        stubToken("t", payload("maria@gmail.com", true, null));

        String jwt = authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t"));

        assertEquals("jwt", jwt);
        User saved = savedUser();
        assertEquals("maria", saved.getUserName());
        assertEquals("maria@gmail.com", saved.getEmail());
    }

    @Test
    void unverifiedEmailIsRejected() throws Exception {
        stubToken("t", payload("eve@gmail.com", false, "Eve"));

        assertThrows(InvalidGoogleTokenException.class,
                () -> authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("t")));
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        when(verifier.verify("bad")).thenReturn(null);

        assertThrows(InvalidGoogleTokenException.class,
                () -> authService.authenticateOrRegisterGoogleUser(new GoogleTokenDTO("bad")));
    }

    private GoogleIdToken.Payload payload(String email, boolean verified, String name) {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload().setEmail(email).setEmailVerified(verified);
        if (name != null) {
            payload.set("name", name);
        }
        return payload;
    }

    private void stubToken(String token, GoogleIdToken.Payload payload) throws Exception {
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        when(idToken.getPayload()).thenReturn(payload);
        when(verifier.verify(token)).thenReturn(idToken);
    }

    private User savedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        return captor.getValue();
    }
}
