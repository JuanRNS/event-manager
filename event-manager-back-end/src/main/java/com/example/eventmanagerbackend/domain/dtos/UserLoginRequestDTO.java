package com.example.eventmanagerbackend.domain.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
        @NotBlank(message = "O username é obrigatório")
        String username,
        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
