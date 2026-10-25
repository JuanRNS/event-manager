package com.example.eventmanagerbackend.domain.dtos;

public record UserResponseDTO(
        Long id,
        String userName,
        String email
) {
}
