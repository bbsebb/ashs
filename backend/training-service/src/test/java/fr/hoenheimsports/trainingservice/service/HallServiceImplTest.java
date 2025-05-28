package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.model.Address;
import fr.hoenheimsports.trainingservice.model.Hall;
import fr.hoenheimsports.trainingservice.repository.HallRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HallServiceImplTest {

    @Mock
    private HallRepository hallRepository;
    

    @InjectMocks
    private HallServiceImpl hallService;

    @Test
    void createHall_shouldReturnHallDTOResponse_whenValidRequest() {
        // Arrange

        Hall hallEntity = getHall();
        Hall savedHall = getHall(1L);
        
        when(hallRepository.save(hallEntity)).thenReturn(savedHall);

        // Act
        Hall result = hallService.createHall(hallEntity);

        // Assert
        assertThat(result).isEqualTo(savedHall);
        verify(hallRepository, times(1)).save(hallEntity);

    }




    @Test
    void getHallById_shouldReturnHallDTOResponse_whenIdExists() {
        // Arrange
        Long hallId = 1L;
        Hall hallResponse = getHall(hallId);
        

        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hallResponse));


        // Act
        Hall result = hallService.getHallById(hallId);
        
        // Assert
        assertThat(result).isEqualTo(hallResponse);
        verify(hallRepository, times(1)).findById(hallId);

    }

    @Test
    void getHallById_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long hallId = 1L;
        when(hallRepository.findById(hallId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> hallService.getHallById(hallId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Hall not found with id: " + hallId);

        verify(hallRepository, times(1)).findById(hallId);

    }

    @Test
    void deleteHall_shouldDeleteHall_whenIdExists() {
        // Arrange
        Long hallId = 1L;
        when(hallRepository.existsById(hallId)).thenReturn(true);

        // Act
        hallService.deleteHall(hallId);

        // Assert
        verify(hallRepository, times(1)).existsById(hallId);
        verify(hallRepository, times(1)).deleteById(hallId);
    }

    @Test
    void deleteHall_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long hallId = 1L;
        when(hallRepository.existsById(hallId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> hallService.deleteHall(hallId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Hall not found with id: " + hallId);

        verify(hallRepository, times(1)).existsById(hallId);
        verify(hallRepository, never()).deleteById(hallId);
    }

    @Test
    void getHalls_shouldReturnPageOfHallDTOResponses_whenValidRequest() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Hall hall1 = getHall(1L);
        Hall hall2 = getHall(2L);


        Page<Hall> hallPage = new PageImpl<>(List.of(hall1, hall2), pageable, 2);


        when(hallRepository.findAll(pageable)).thenReturn(hallPage);



        // Act
        Page<Hall> result = hallService.getHalls(pageable);

        // Assert
        assertThat(result).isEqualTo(hallPage);
        verify(hallRepository, times(1)).findAll(pageable);



    }

    @Test
    void updateHall_shouldReturnUpdatedHallDTOResponse_whenIdAndRequestAreValid() {
        // Arrange
        Long hallId = 1L;

        Hall existingHall = getHall(hallId);
        Hall updatedEntity = getHall(hallId);
        updatedEntity.setName("updatedName");
        updatedEntity.setAddress(Address.builder()
                .street("updatedStreet")
                .city("updatedCity")
                .postalCode("updatedPostalCode")
                .country("updatedCountry")
                .build());


        when(hallRepository.findById(hallId)).thenReturn(Optional.of(existingHall));

        when(hallRepository.save(existingHall)).thenReturn(updatedEntity);


        // Act
        Hall result = hallService.updateHall(hallId, updatedEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(hallId);
        assertThat(result.getName()).isEqualTo(updatedEntity.getName());
        assertThat(result.getAddress().getStreet()).isEqualTo(updatedEntity.getAddress().getStreet());
        assertThat(result.getAddress().getCity()).isEqualTo(updatedEntity.getAddress().getCity());
        assertThat(result.getAddress().getPostalCode()).isEqualTo(updatedEntity.getAddress().getPostalCode());
        assertThat(result.getAddress().getCountry()).isEqualTo(updatedEntity.getAddress().getCountry());

        verify(hallRepository, times(1)).findById(hallId);
        verify(hallRepository, times(1)).save(existingHall);

    }

    @Test
    void updateHall_shouldThrowEntityNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long hallId = 1L;
        Hall updatedHall = getHall(hallId);
        when(hallRepository.findById(hallId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> hallService.updateHall(hallId, updatedHall))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Hall not found with id: " + hallId);

        verify(hallRepository, times(1)).findById(hallId);
        verify(hallRepository, never()).save(updatedHall);


    }

    private static Hall getHall(Long hallId) {
        return Hall.builder()
                .id(hallId)
                .name("Test Hall")
                .address(getAddress())
                .build();
    }

    private static Hall getHall() {
        return Hall.builder()
                .name("Test Hall")
                .address(getAddress())
                .build();
    }

    private static Address getAddress() {
        return Address.builder()
                .street("123 Test Street")
                .city("Test City")
                .postalCode("12345")
                .country("Test Country")
                .build();
    }



}