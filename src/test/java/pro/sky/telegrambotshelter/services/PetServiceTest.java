package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.repository.PetRepository;
import pro.sky.telegrambotshelter.service.PetService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PetService.class})
@ExtendWith(SpringExtension.class)
public class PetServiceTest {
    @Autowired
    private PetService petService;
    @MockBean
    private PetRepository petRepository;
    @MockBean
    private Shelter shelter;
    @MockBean
    private ShelterService shelterService;
    private Pet pet;
    private List<Pet> pets;

    @BeforeEach
    public void initEach() {
        pet = mock(Pet.class);
        shelter = mock(Shelter.class);
    }

    @Test
    public void createPet(){
        when(petRepository.save(pet)).thenReturn(pet);

        Pet actual = petService.createPet(pet);
        assertEquals(pet, actual);
    }

    @Test
    public void updatePet(){
        when(petRepository.existsById(pet.getId())).thenReturn(true);
        when(petRepository.save(pet)).thenReturn(pet);

        Optional<Pet> actual = petService.updatePet(pet);
        assertEquals(Optional.of(pet), actual);
    }

    @Test
    public void getAllPets(){
        when(petRepository.findAll()).thenReturn(pets);

        List<Pet> actual = petService.getAllPets();
        assertEquals(pets, actual);
    }

    @Test
    public void getAllByShelterId(){
        when(shelterService.getShelterById(shelter.getId())).thenReturn(Optional.ofNullable(shelter));
        when(petRepository.findAllByShelter(shelter)).thenReturn(pets);

        List<Pet> actual = petService.getAllByShelterId(shelter.getId());
        assertEquals(pets, actual);
    }

    @Test
    public void getAllByShelterId_WithNotFoundException(){
        when(shelterService.getShelterById(anyLong())).thenReturn(Optional.empty());

        String expectedMessage = "Shelter not found";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> petService.getAllByShelterId(anyLong())
        );

        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    public void getPetById(){
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        Optional<Pet> actual = petService.getPetById(pet.getId());
        assertEquals(Optional.of(pet), actual);
    }

    @Test
    public void deletePet(){
        when(petRepository.existsById(pet.getId())).thenReturn(true);

        petService.deletePet(pet.getId());

        verify(petRepository).deleteById(pet.getId());
    }

    @Test
    public void deletePet_WithNotFoundException(){
        when(petRepository.existsById(anyLong())).thenReturn(false);

        String expectedMessage = "Pet id not found";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> petService.deletePet(anyLong())
        );

        assertEquals(expectedMessage, exception.getMessage());
    }
}
