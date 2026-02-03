// infrastructure/services/AuthService.java
package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.GoogleTokenDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String authenticateOrRegisterGoogleUser(GoogleTokenDTO tokenDTO)
            throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyGoogleToken(tokenDTO.token());

        if (idToken == null) {
            throw new RuntimeException("Token do Google inválido ou expirado.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return jwtService.generateToken(existingUser.get());
        } else {
            User newUser = registerGoogleUser(email, name);
            return jwtService.generateToken(newUser);
        }
    }

    private GoogleIdToken verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        return verifier.verify(idTokenString);
    }

    private User registerGoogleUser(String email, String name) {
        User newUser = new User();

        String userName = name.replaceAll("\\s+", "").toLowerCase();

        newUser.setPassword(passwordEncoder.encode("GOOGLE_SSO_PASSWORD_PLACEHOLDER"));

        newUser.setEmail(email);
        newUser.setUserName(userName);

        return userRepository.save(newUser);
    }
}