package com.example.eventmanagerbackend.infrastructure.controllers;

import com.example.eventmanagerbackend.domain.dtos.UserLoginRequestDTO;
import com.example.eventmanagerbackend.domain.dtos.UserRequestDTO;
import com.example.eventmanagerbackend.domain.dtos.UserResponseDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.security.AuthenticationService;
import com.example.eventmanagerbackend.infrastructure.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponseDTO(createdUser.getId(), createdUser.getUserName(), createdUser.getEmail()));
    }

    @PostMapping("login")
    public String login(@RequestBody @Valid UserLoginRequestDTO user) {
        return authenticationService.authenticate(user);
    }

}
