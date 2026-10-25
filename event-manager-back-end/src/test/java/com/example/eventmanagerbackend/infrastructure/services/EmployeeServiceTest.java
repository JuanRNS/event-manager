package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.EmployeeRequestUpdateDTO;
import com.example.eventmanagerbackend.domain.entities.Employee;
import com.example.eventmanagerbackend.domain.entities.EmployeeType;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.EmployeeNotFoundException;
import com.example.eventmanagerbackend.infrastructure.exceptions.PartyNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.EmployeeMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.EmployeeRepository;
import com.example.eventmanagerbackend.infrastructure.repositories.PartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final Long FOREIGN_ID = 99L;

    @Mock private EmployeeMapper employeeMapper;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeTypeService employeeTypeService;
    @Mock private UserService userService;
    @Mock private PartyRepository partyRepository;
    @InjectMocks private EmployeeService employeeService;

    private final User currentUser = new User();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    private void stubForeignEmployee() {
        when(employeeRepository.findByIdAndUser(FOREIGN_ID, currentUser)).thenReturn(Optional.empty());
    }

    private void verifyNoWrites() {
        verify(employeeRepository, never()).save(any());
        verify(employeeRepository, never()).delete(any());
        verifyNoInteractions(partyRepository);
    }

    @Test
    void getByIdOfAnotherUserThrowsNotFound() {
        stubForeignEmployee();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(FOREIGN_ID));
    }

    @Test
    void updateOfAnotherUserThrowsNotFound() {
        stubForeignEmployee();
        EmployeeRequestUpdateDTO dto = new EmployeeRequestUpdateDTO("n", "p", "1", null, null);
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(FOREIGN_ID, dto));
        verifyNoWrites();
    }

    @Test
    void deleteOfAnotherUserThrowsNotFound() {
        stubForeignEmployee();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(FOREIGN_ID));
        verifyNoWrites();
    }

    @Test
    void partiesOfAnotherUsersEmployeeThrowsNotFound() {
        stubForeignEmployee();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeePartyByIdEmployee(FOREIGN_ID));
    }

    @Test
    void employeesOfAnotherUsersPartyThrowsNotFound() {
        when(partyRepository.findByIdAndUser(FOREIGN_ID, currentUser)).thenReturn(Optional.empty());
        assertThrows(PartyNotFoundException.class, () -> employeeService.getEmployeeByPartyId(FOREIGN_ID));
    }

    @Test
    void updateOwnEmployeeSaves() {
        Employee employee = new Employee();
        employee.setEmployeeType(new EmployeeType(1L, "garcom", currentUser));
        when(employeeRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(employee));
        EmployeeRequestUpdateDTO dto = new EmployeeRequestUpdateDTO("n", "p", "1", 1L, null);

        employeeService.updateEmployee(1L, dto);

        verify(employeeMapper).updateEmployeeFromDTO(dto, employee);
        verify(employeeRepository).save(employee);
    }
}
