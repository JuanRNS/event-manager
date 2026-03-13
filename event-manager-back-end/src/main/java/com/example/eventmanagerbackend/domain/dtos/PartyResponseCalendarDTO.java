package com.example.eventmanagerbackend.domain.dtos;

import java.time.LocalDate;

public record PartyResponseCalendarDTO(
        Long id,
        String nameClient,
        String location,
        LocalDate date,
        String hourStart,
        String hourEnd
) {
}
