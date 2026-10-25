package com.example.eventmanagerbackend.infrastructure.services;

import com.example.eventmanagerbackend.domain.dtos.MaterialRequestDTO;
import com.example.eventmanagerbackend.domain.entities.Material;
import com.example.eventmanagerbackend.domain.entities.User;
import com.example.eventmanagerbackend.infrastructure.exceptions.MaterialNotFoundException;
import com.example.eventmanagerbackend.infrastructure.mappers.MaterialMapper;
import com.example.eventmanagerbackend.infrastructure.repositories.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    private static final Long FOREIGN_ID = 99L;

    @Mock private MaterialRepository materialRepository;
    @Mock private MaterialMapper materialMapper;
    @Mock private UserService userService;
    @InjectMocks private MaterialService materialService;

    private final User currentUser = new User();

    @BeforeEach
    void setUp() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    private void stubForeignMaterial() {
        when(materialRepository.findByIdAndUser(FOREIGN_ID, currentUser)).thenReturn(Optional.empty());
    }

    private void verifyNoWrites() {
        verify(materialRepository, never()).save(any());
        verify(materialRepository, never()).delete(any());
        verify(materialRepository, never()).deleteById(anyLong());
    }

    @Test
    void getByIdOfAnotherUserThrowsNotFound() {
        stubForeignMaterial();
        assertThrows(MaterialNotFoundException.class, () -> materialService.getMaterialById(FOREIGN_ID));
    }

    @Test
    void updateOfAnotherUserThrowsNotFound() {
        stubForeignMaterial();
        assertThrows(MaterialNotFoundException.class,
                () -> materialService.updateMaterial(FOREIGN_ID, new MaterialRequestDTO("x")));
        verifyNoWrites();
    }

    @Test
    void deleteOfAnotherUserThrowsNotFound() {
        stubForeignMaterial();
        assertThrows(MaterialNotFoundException.class, () -> materialService.deleteMaterial(FOREIGN_ID));
        verifyNoWrites();
    }

    @Test
    void updateOwnMaterialSaves() {
        Material material = new Material("old");
        when(materialRepository.findByIdAndUser(1L, currentUser)).thenReturn(Optional.of(material));

        materialService.updateMaterial(1L, new MaterialRequestDTO("new"));

        assertEquals("new", material.getDescription());
        verify(materialRepository).save(material);
    }
}
