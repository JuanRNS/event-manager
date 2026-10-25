package com.example.eventmanagerbackend.domain.dtos;

import jakarta.validation.constraints.NotBlank;

public record GoogleTokenDTO(
    @NotBlank(message = "O token do Google é obrigatório")
    String token
) {
}
