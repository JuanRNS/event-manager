package com.example.eventmanagerbackend.infrastructure.security;

import com.example.eventmanagerbackend.domain.dtos.UserLoginRequestDTO;
import com.example.eventmanagerbackend.infrastructure.exceptions.TooManyLoginAttemptsException;
import com.example.eventmanagerbackend.infrastructure.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptLimiter loginAttemptLimiter;

    public AuthenticationService(JwtService jwtService, AuthenticationManager authenticationManager, LoginAttemptLimiter loginAttemptLimiter) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.loginAttemptLimiter = loginAttemptLimiter;
    }

    public String authenticate(UserLoginRequestDTO user) {
        if (!loginAttemptLimiter.tryAcquire(user.username())) {
            throw new TooManyLoginAttemptsException();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.username(), user.password())
        );
        loginAttemptLimiter.reset(user.username());
        return jwtService.generateToken(authentication);
    }

}
