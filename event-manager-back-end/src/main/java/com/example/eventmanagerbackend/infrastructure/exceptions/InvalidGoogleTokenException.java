package com.example.eventmanagerbackend.infrastructure.exceptions;

public class InvalidGoogleTokenException extends RuntimeException {
    public InvalidGoogleTokenException() {
        super("Token do Google inválido.");
    }
}
