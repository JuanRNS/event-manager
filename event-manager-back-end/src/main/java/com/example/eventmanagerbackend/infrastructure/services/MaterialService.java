package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.MaterialRequestDTO;
import com.example.eventmanagerbackend.domain.dtos.MaterialResponseDTO;
import com.example.eventmanagerbackend.domain.entities.Material;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.MaterialNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.MaterialMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final UserService userService;

    public List<MaterialResponseDTO> getAllMaterials() {
        User user = userService.getCurrentUser();
        return materialRepository
                .findAllByUser(user)
                .stream().map(materialMapper::toMaterialResponseDTO)
                .toList();
    }

    public MaterialResponseDTO getMaterialDTOById(Long id) {
        return materialMapper.toMaterialResponseDTO(getMaterialById(id));
    }

    public Material getMaterialById(Long id) {
        User user = userService.getCurrentUser();
        return materialRepository.findByIdAndUser(id, user)
                .orElseThrow(MaterialNotFoundException::new);
    }

    public MaterialResponseDTO createMaterial(MaterialRequestDTO material) {
        if (material == null) {
            throw new MaterialNotFoundException();
        }
        User user = userService. getCurrentUser();
        Material newMaterial = new Material();
        newMaterial.setUser(user);
        newMaterial.setDescription(material.description());
        Material savedMaterial = materialRepository.save(newMaterial);
        return materialMapper.toMaterialResponseDTO(savedMaterial);
    }

    public void updateMaterial(Long id, MaterialRequestDTO materialRequest) {
        Material material = getMaterialById(id);
        material.setDescription(materialRequest.description());
        materialRepository.save(material);
    }

    public void deleteMaterial(Long id) {
        materialRepository.delete(getMaterialById(id));
    }

}
