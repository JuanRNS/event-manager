package com.example.eventmanagerbackend.infrastructure.controllers;

import com.example.eventmanagerbackend.domain.dtos.GoogleTokenDTO;
import com.example.eventmanagerbackend.infrastructure.exceptions.InvalidGoogleTokenException;
import com.example.eventmanagerbackend.infrastructure.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<String> googleLogin(@RequestBody GoogleTokenDTO tokenDTO) {
        try {
            String jwt = authService.authenticateOrRegisterGoogleUser(tokenDTO);
            return ResponseEntity.ok(jwt);
        } catch (InvalidGoogleTokenException | GeneralSecurityException | IllegalArgumentException e) {
            log.warn("Google token rejected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido.");
        } catch (IOException | RuntimeException e) {
            log.error("Google login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao autenticar com o Google.");
        }
    }
}
