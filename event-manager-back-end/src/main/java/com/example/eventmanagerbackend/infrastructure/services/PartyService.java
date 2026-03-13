package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.*;
import com.example.eventmanagerbackend.domain.entities.*;
import com.example.eventmanagerbackend.domain.enums.StatusParty;
import com.example.eventmanagerbackend.infrastructure.exceptions.EmployeeNotFoundException;
import com.example.eventmanagerbackend.infrastructure.exceptions.PartyNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.PartyMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final PartyMapper partyMapper;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final MaterialService materialService;
    private final EmployeeTypeService employeeTypeService;

    public PartyResponseDTO createParty(PartyRequestDTO partyRequestDTO) {
        User user = userService.getCurrentUser();
        Material material = materialService.getMaterialById(partyRequestDTO.idMaterial());

        Party party = partyMapper.toEntity(partyRequestDTO);

        party.setMaterial(material);
        party.setUser(user);

        Party savedParty = partyRepository.save(party);
        return partyMapper.toResponseDTO(savedParty);
    }

    public PartyResponseDTO getPartyResponseDTOById(Long id) {
        return partyRepository.findById(id)
                .map(partyMapper::toResponseDTO)
                .orElseThrow(PartyNotFoundException::new);
    }

    public void updateParty(Long id, PartyUpdateRequestDTO dto) {
        Party party = partyRepository.findById(id)
                .orElseThrow(PartyNotFoundException::new);

        partyMapper.updatePartyFromDTO(dto, party);

        if (dto.idMaterial() != null && !dto.idMaterial().equals(party.getMaterial().getId())) {
            Material material = materialService.getMaterialById(dto.idMaterial());
            party.setMaterial(material);
        }

        updatePartyValues(party, dto.values());

        partyRepository.save(party);
    }

    public void deleteParty(Long id) {
        if (!partyRepository.existsById(id)) {
            throw new PartyNotFoundException();
        }
        partyRepository.deleteById(id);
    }

    public Page<PartyResponseDTO> getAllPartiesByStatus(Pageable pageable) {
        User user = userService.getCurrentUser();
        return partyRepository.findAllByUserAndStatus(user, StatusParty.AGENDADA, pageable)
                .map(partyMapper::toResponseDTO);
    }

    public Page<PartyResponseDTO> getAllParties(Pageable pageable) {
        User user = userService.getCurrentUser();
        return partyRepository.findAllByUser(user, pageable)
                .map(partyMapper::toResponseDTO);
    }

    public void createEmployeePartiesValues(Long idParty, EmployeePartiesValuesDTO dto) {
        Party party = partyRepository.findById(idParty).orElseThrow(PartyNotFoundException::new);
        EmployeeType employeeType = employeeTypeService.getEmployeeTypeById(dto.idEmployeeType());

        EmployeePartiesValues values = new EmployeePartiesValues(employeeType, party, dto.value());
        party.addValues(values);

        partyRepository.save(party);
    }

    public PartyEmployeeViewDTO getPartyEmployeeById(Long idParty) {
        Party party = partyRepository.findById(idParty).orElseThrow(PartyNotFoundException::new);

        List<Long> employeeIds = party.getPartyEmployees().stream()
                .map(Employee::getId)
                .toList();

        List<EmployeeIdResponseDTO> employeeList = employeeService.getAllEmployees(employeeIds);

        MaterialResponseDTO materialDTO = partyMapper.toMaterialResponseDTO(party.getMaterial());

        return new PartyEmployeeViewDTO(
                party.getId(),
                party.getLocation(),
                party.getNameClient(),
                party.getDate(),
                party.getValues(),
                materialDTO,
                party.getNumberOfPeople(),
                employeeList,
                party.getStatus()
        );
    }

    public void addEmployeeParty(PartyEmployeeAddRequestDTO dto) {
        Party party = partyRepository.findById(dto.partyId()).orElseThrow(PartyNotFoundException::new);

        List<Employee> employeeList = employeeService.getEmployeeByIds(dto.employeeIds());

        if (employeeList.size() != dto.employeeIds().size()) {
            throw new EmployeeNotFoundException();
        }

        party.setPartyEmployees(employeeList);
        partyRepository.save(party);
    }

    public Long countPartiesByStatusAndUser() {
        return partyRepository.countByStatusAndUser(StatusParty.REALIZADA, userService.getCurrentUser());
    }

    public List<StatusResponseDTO> getStatus() {
        return Arrays.stream(StatusParty.values())
                .map(status -> new StatusResponseDTO(status.name(), status.name()))
                .toList();
    }

    public List<PartyResponseCalendarDTO> getPartiesForCalendar() {
        User user = userService.getCurrentUser();
        List<Party> parties = partyRepository.findAllByUser(user);
        return parties.stream()
                .map(partyMapper::toResponseCalendarDTO)
                .toList();
    }

    private void updatePartyValues(Party party, List<EmployeePartiesValuesResponseDTO> valuesDtos) {
        if (valuesDtos == null) return;

        party.getValues().clear();

        List<EmployeePartiesValues> newValues = valuesDtos.stream()
                .map(v -> {
                    EmployeeType type = employeeTypeService.getEmployeeTypeById(v.employeeType().getId());
                    return new EmployeePartiesValues(type, party, v.value());
                }).toList();

        party.getValues().addAll(newValues);
    }
}
