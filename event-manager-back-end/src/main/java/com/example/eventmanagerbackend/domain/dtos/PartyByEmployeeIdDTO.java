package com.example.eventmanagerbackend.domain.dtos;

import com.example.eventmanagerbackend.domain.enums.StatusParty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PartyByEmployeeIdDTO(
        String location,
        String nameClient,
        LocalDateTime date,
        BigDecimal valuePerDay,
        StatusParty status,
        Long numberOfPeople,
        String hourStart,
        String hourEnd
) {
}
