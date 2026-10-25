package com.example.eventmanagerbackend.infrastructure.exceptions;

public class TooManyLoginAttemptsException extends RuntimeException {
    public TooManyLoginAttemptsException() {
        super("Muitas tentativas de login. Tente novamente mais tarde.");
    }
}
