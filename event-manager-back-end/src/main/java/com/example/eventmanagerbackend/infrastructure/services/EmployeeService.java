package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.*;
import com.example.eventmanagerbackend.domain.entities.*;
import com.example.eventmanagerbackend.domain.enums.StatusEmployee;
import com.example.eventmanagerbackend.infrastructure.exceptions.PartyNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.EmployeeMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.EmployeeRepository;
import com.example.eventmanagerbackend.infrastructure.repositories.PartyRepository;
import com.example.eventmanagerbackend.infrastructure.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeTypeService employeeTypeService;
    private final UserService userService;
    private final PartyRepository partyRepository;

    public void createEmployee(EmployeeRequestDTO dto) {
        User user = userService.getCurrentUser();
        EmployeeType employeeType = employeeTypeService.getEmployeeTypeById(dto.idEmployeeType());

        Employee employee = employeeMapper.toEntity(dto);

        employee.setUser(user);
        employee.setEmployeeType(employeeType);

        employeeRepository.save(employee);
    }

    public void updateEmployee(Long id, EmployeeRequestUpdateDTO dto) {
        User user = userService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, user);

        employeeMapper.updateEmployeeFromDTO(dto, employee);

        if (dto.idEmployeeType() != null && !dto.idEmployeeType().equals(employee.getEmployeeType().getId())) {
            EmployeeType newType = employeeTypeService.getEmployeeTypeById(dto.idEmployeeType());
            employee.setEmployeeType(newType);
        }

        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        User user = userService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, user);

        List<Party> parties = new ArrayList<>(employee.getEmployeeParties());
        for (Party party : parties) {
            party.getPartyEmployees().remove(employee);
            employee.getEmployeeParties().remove(party);
            partyRepository.save(party);
        }

        employeeRepository.delete(employee);
    }

    public Page<EmployeeIdResponseDTO> getAllEmployee(Pageable pageable) {
        User user = userService.getCurrentUser();
        return employeeRepository.findAllByUser(user, pageable)
                .map(employeeMapper::toEmployeeIdResponseDTO);
    }

    public EmployeeIdResponseDTO getEmployeeById(Long id) {
        User user = userService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, user);
        return employeeMapper.toEmployeeIdResponseDTO(employee);
    }

    public List<EmployeeIdResponseDTO> getAllEmployees(List<Long> ids) {
        User user = userService.getCurrentUser();
        List<Employee> employees = employeeRepository.findAllByIdAndUser(user, ids);
        return employeeMapper.toEmployeeIdResponseDTOList(employees);
    }

    public List<Employee> getEmployeeByIds(List<Long> ids) {
        return employeeRepository.findAllByIdAndUser(userService.getCurrentUser(), ids);
    }

    public Page<EmployeeAddResponseDTO> getEmployeeAddResponseDTO(Pageable pageable) {
        User user = userService.getCurrentUser();
        return employeeRepository.findAllByStatusEmployeeAndUser(StatusEmployee.ATIVO, user, pageable)
                .map(employeeMapper::toEmployeeAddResponseDTO);
    }


    public EmployeePartyResponseDTO getEmployeePartyByIdEmployee(Long id) {
        User user = userService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, user);

        return new EmployeePartyResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getPixKey(),
                employee.getPhone(),
                employee.getStatusEmployee(),
                employee.getEmployeeType(),
                employee.getEmployeeParties()
        );
    }

    public EmployeePartyDTO getEmployeeParties(Long id) {
        User user = userService.getCurrentUser();
        Employee employee = employeeRepository.findByIdAndUser(id, user);

        List<PartyByEmployeeIdDTO> partiesDtos = calculatePartiesValuesForEmployee(employee);

        return new EmployeePartyDTO(
                employee.getName(),
                employee.getPhone(),
                employee.getPixKey(),
                partiesDtos
        );
    }

    public Page<EmployeeResponseDashboardDTO> getEmployeeDashboard(Pageable pageable) {
        User user = userService.getCurrentUser();
        Page<Employee> employees = employeeRepository.findAllByUser(user, pageable);

        return employees.map(employee -> calculateDashboardData(employee, null, null));
    }

    public Page<EmployeeResponseDashboardDTO> getEmployeeDashboardByDate(Pageable pageable, LocalDate fromDate, LocalDate toDate) {
        User user = userService.getCurrentUser();
        Page<Employee> employees = employeeRepository.findAllByUser(user, pageable);

        return employees.map(employee -> calculateDashboardData(employee, fromDate, toDate));
    }

    public List<Long> getEmployeeByPartyId(Long id) {
        Party party = partyRepository.findById(id).orElseThrow(PartyNotFoundException::new);
        User currentUser = userService.getCurrentUser();

        if (!party.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Insufficient privileges to access");
        }

        return party.getPartyEmployees().stream()
                .map(Employee::getId)
                .toList();
    }

    public Long countEmployeesByStatusEmployee() {
        return employeeRepository.countByStatusEmployeeAndUser(StatusEmployee.ATIVO, userService.getCurrentUser());
    }

    public List<StatusResponseDTO> getStatus() {
        return Arrays.stream(StatusEmployee.values())
                .map(status -> new StatusResponseDTO(status.name(), status.name()))
                .toList();
    }

    private List<PartyByEmployeeIdDTO> calculatePartiesValuesForEmployee(Employee employee) {
        return employee.getEmployeeParties().stream()
                .map(party -> {
                    BigDecimal value = calculatePartyValueForType(party, employee.getEmployeeType());
                    return employeeMapper.toPartyByEmployeeIdDTO(
                            party.getLocation(),
                            party.getNameClient(),
                            party.getDate(),
                            value,
                            party.getStatus(),
                            party.getNumberOfPeople(),
                            party.getHourStart(),
                            party.getHourEnd()
                    );
                })
                .toList();
    }

    private EmployeeResponseDashboardDTO calculateDashboardData(Employee employee, LocalDate from, LocalDate to) {
        List<Party> filteredParties = employee.getEmployeeParties().stream()
                .filter(party -> {
                    if (from == null || to == null) {
                        return DateUtils.initialWeek(party.getDate());
                    }
                    LocalDate pDate = party.getDate().toLocalDate();
                    return (pDate.isEqual(from) || pDate.isAfter(from)) &&
                            (pDate.isEqual(to) || pDate.isBefore(to));
                })
                .toList();

        BigDecimal totalSalary = filteredParties.stream()
                .map(party -> calculatePartyValueForType(party, employee.getEmployeeType()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return employeeMapper.toDashboardDTO(
                employee.getId(),
                employee.getName(),
                (long) filteredParties.size(),
                totalSalary,
                employee.getEmployeeType()
        );
    }

    private BigDecimal calculatePartyValueForType(Party party, EmployeeType type) {
        return party.getValues().stream()
                .filter(v -> v.getEmployeeType().equals(type))
                .map(EmployeePartiesValues::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
