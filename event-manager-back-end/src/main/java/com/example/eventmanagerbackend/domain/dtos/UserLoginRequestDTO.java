package com.example.eventmanagerbackend.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDTO(
        @NotBlank(message = "O username é obrigatório")
        @Size(max = 50, message = "O username deve ter no máximo 50 caracteres")
        String username,
        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
