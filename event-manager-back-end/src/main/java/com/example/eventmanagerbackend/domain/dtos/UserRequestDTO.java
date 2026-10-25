package com.example.eventmanagerbackend.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "O username é obrigatório")
    @Size(max = 50, message = "O username deve ter no máximo 50 caracteres")
    String userName,
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
    String password
) {
}
