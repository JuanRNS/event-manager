package com.example.eventmanagerbackend.domain.dtos;


import java.time.LocalDateTime;

public record PartyRequestDTO(
        String nameClient,
        String location,
        LocalDateTime date,
        Long idMaterial,
        Long numberOfPeople,
        String hourStart,
        String hourEnd
) {
}
