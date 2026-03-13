package com.example.eventmanagerbackend.infrastructure.mappers;

import com.example.eventmanagerbackend.domain.dtos.*;
import com.example.eventmanagerbackend.domain.entities.Employee;
import com.example.eventmanagerbackend.domain.entities.EmployeeType;
import com.example.eventmanagerbackend.domain.enums.StatusParty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "employeeType", ignore = true) // Setado no Service
    @Mapping(target = "employeeParties", ignore = true)
    @Mapping(target = "statusEmployee", constant = "ATIVO") // Default na criação
    Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "employeeParties", ignore = true)
    @Mapping(target = "employeeType", ignore = true) // Tratado no service (pode mudar)
    void updateEmployeeFromDTO(EmployeeRequestUpdateDTO dto, @MappingTarget Employee entity);
    EmployeeIdResponseDTO toEmployeeIdResponseDTO(Employee employee);

    EmployeeOptionsResponseDTO toEmployeeOptionsResponseDTO(Employee employee);

    EmployeePartyResponseDTO toEmployeeResponseDTO(Employee employee);

    EmployeeAddResponseDTO toEmployeeAddResponseDTO(Employee employee);

    List<EmployeeIdResponseDTO> toEmployeeIdResponseDTOList(List<Employee> employees);

    @Mapping(source = "employeeParties", target = "parties", ignore = true) // Ignora pois a lógica é complexa (filtro por tipo)
    EmployeePartyDTO toEmployeePartyDTO(Employee employee);

    @Mapping(source = "value", target = "valuePerDay")
    PartyByEmployeeIdDTO toPartyByEmployeeIdDTO(String location, String nameClient, java.time.LocalDateTime date, BigDecimal value, StatusParty status, Long numberOfPeople, String hourStart, String hourEnd);
    @Mapping(source = "countParties", target = "totalParties")
    @Mapping(source = "totalSalary", target = "valueTotal")
    @Mapping(source = "employeeType", target = "employeeType")
    EmployeeResponseDashboardDTO toDashboardDTO(Long id, String name, Long countParties, BigDecimal totalSalary, EmployeeType employeeType);
}
