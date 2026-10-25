package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.EmployeeTypeRequestDTO;
import com.example.eventmanagerbackend.domain.entities.EmployeeType;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.EmployeeTypeNotFoundException;
import com.example.eventmanagerbackend.infrastructure.repositories.EmployeeTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeTypeServiceTest {

    private static final Long FOREIGN_ID = 99L;

    @Mock private EmployeeTypeRepository employeeTypeRepository;
    @Mock private UserService userService;
    @InjectMocks private EmployeeTypeService employeeTypeService;

    private final User currentUser = new User();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    private void stubForeignType() {
        when(employeeTypeRepository.findByIdAndUser(FOREIGN_ID, currentUser)).thenReturn(Optional.empty());
    }

    private void verifyNoWrites() {
        verify(employeeTypeRepository, never()).save(any());
        verify(employeeTypeRepository, never()).delete(any());
        verify(employeeTypeRepository, never()).deleteById(anyLong());
    }

    @Test
    void getByIdOfAnotherUserThrowsNotFound() {
        stubForeignType();
        assertThrows(EmployeeTypeNotFoundException.class, () -> employeeTypeService.getEmployeeTypeById(FOREIGN_ID));
    }

    @Test
    void updateOfAnotherUserThrowsNotFound() {
        stubForeignType();
        assertThrows(EmployeeTypeNotFoundException.class,
                () -> employeeTypeService.updateEmployeeType(FOREIGN_ID, new EmployeeTypeRequestDTO("x")));
        verifyNoWrites();
    }

    @Test
    void deleteOfAnotherUserThrowsNotFound() {
        stubForeignType();
        assertThrows(EmployeeTypeNotFoundException.class, () -> employeeTypeService.deleteEmployeeType(FOREIGN_ID));
        verifyNoWrites();
    }

    @Test
    void deleteOwnTypeDeletesLoadedEntity() {
        EmployeeType type = new EmployeeType();
        when(employeeTypeRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(type));

        employeeTypeService.deleteEmployeeType(1L);

        verify(employeeTypeRepository).delete(type);
    }
}
