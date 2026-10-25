package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.EmployeePartiesValuesDTO;
import com.example.eventmanagerbackend.domain.dtos.PartyEmployeeAddRequestDTO;
import com.example.eventmanagerbackend.domain.dtos.PartyUpdateRequestDTO;
import com.example.eventmanagerbackend.domain.entities.Party;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.PartyNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.PartyMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.PartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

    private static final Long FOREIGN_ID = 99L;

    @Mock private PartyRepository partyRepository;
    @Mock private PartyMapper partyMapper;
    @Mock private EmployeeService employeeService;
    @Mock private UserService userService;
    @Mock private MaterialService materialService;
    @Mock private EmployeeTypeService employeeTypeService;
    @InjectMocks private PartyService partyService;

    private final User currentUser = new User();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    private void stubForeignParty() {
        when(partyRepository.findByIdAndUser(FOREIGN_ID, currentUser)).thenReturn(Optional.empty());
    }

    private void verifyNoWrites() {
        verify(partyRepository, never()).save(any());
        verify(partyRepository, never()).delete(any());
        verify(partyRepository, never()).deleteById(anyLong());
    }

    @Test
    void getByIdOfAnotherUserThrowsNotFound() {
        stubForeignParty();
        assertThrows(PartyNotFoundException.class, () -> partyService.getPartyResponseDTOById(FOREIGN_ID));
    }

    @Test
    void updateOfAnotherUserThrowsNotFound() {
        stubForeignParty();
        PartyUpdateRequestDTO dto = new PartyUpdateRequestDTO("c", "l", null, List.of(), 1L, 1L, null);
        assertThrows(PartyNotFoundException.class, () -> partyService.updateParty(FOREIGN_ID, dto));
        verifyNoWrites();
    }

    @Test
    void deleteOfAnotherUserThrowsNotFound() {
        stubForeignParty();
        assertThrows(PartyNotFoundException.class, () -> partyService.deleteParty(FOREIGN_ID));
        verifyNoWrites();
    }

    @Test
    void viewOfAnotherUserThrowsNotFound() {
        stubForeignParty();
        assertThrows(PartyNotFoundException.class, () -> partyService.getPartyEmployeeById(FOREIGN_ID));
        verifyNoInteractions(employeeService);
    }

    @Test
    void addValuesToAnotherUsersPartyThrowsNotFound() {
        stubForeignParty();
        EmployeePartiesValuesDTO dto = new EmployeePartiesValuesDTO(1L, BigDecimal.TEN);
        assertThrows(PartyNotFoundException.class, () -> partyService.createEmployeePartiesValues(FOREIGN_ID, dto));
        verifyNoWrites();
    }

    @Test
    void addEmployeesToAnotherUsersPartyThrowsNotFound() {
        stubForeignParty();
        PartyEmployeeAddRequestDTO dto = new PartyEmployeeAddRequestDTO(List.of(), FOREIGN_ID);
        assertThrows(PartyNotFoundException.class, () -> partyService.addEmployeeParty(dto));
        verifyNoWrites();
    }

    @Test
    void deleteOwnPartyDeletesLoadedEntity() {
        Party party = new Party();
        when(partyRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(party));

        partyService.deleteParty(1L);

        verify(partyRepository).delete(party);
    }
}
