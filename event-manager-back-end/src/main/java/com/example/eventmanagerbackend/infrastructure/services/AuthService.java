package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.GoogleTokenDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.InvalidGoogleTokenException;
import com.example.eventmanagerbackend.infrastructure.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public String authenticateOrRegisterGoogleUser(GoogleTokenDTO tokenDTO)
            throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = googleIdTokenVerifier.verify(tokenDTO.token());
        if (idToken == null) {
            throw new InvalidGoogleTokenException();
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        if (email == null || !Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new InvalidGoogleTokenException();
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> registerGoogleUser(email));
        return jwtService.generateToken(user);
    }

    private User registerGoogleUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUserName(uniqueUserName(email));
        newUser.setPassword(passwordEncoder.encode(randomPassword()));
        return userRepository.save(newUser);
    }

    private String uniqueUserName(String email) {
        String base = Normalizer.normalize(email.substring(0, email.indexOf('@')), Normalizer.Form.NFD)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "")
                .replaceFirst("^[0-9]+", "");
        if (base.isEmpty()) {
            base = "user";
        }
        String candidate = base;
        for (int suffix = 1; userRepository.findByUserName(candidate).isPresent(); suffix++) {
            candidate = base + suffix;
        }
        return candidate;
    }

    private static String randomPassword() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
