package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.UserRequestDTO;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.*;
import com.example.eventmanagerbackend.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDTO user) {
        if(!validateUserName(user.userName())) {
            throw new UserNameStringFirstException();
        }
        if (validateEmail(user.email())) {
            throw new EmailExistsException();
        }
        if (validateName(user.userName())) {
            throw new UserNameExistsException();
        }
        User createdUser = parseUser(user);
        return userRepository.save(createdUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("Usuário não autorizado.");
        }

        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
    }

    private User parseUser(UserRequestDTO user) {
        User createdUser = new User();
        createdUser.setEmail(user.email());
        createdUser.setUserName(user.userName());
        createdUser.setPassword(passwordEncoder.encode(user.password()));
        return createdUser;
    }

    private boolean validateUserName(String userName) {
        return userName != null && userName.matches("^[A-Za-z].*");
    }

    private boolean validateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean validateName(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }
}
