package pro.sky.telegrambotshelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.telegrambotshelter.model.Pet;
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.service.PetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    private Pet pet;
    private Shelter shelter;

    @BeforeEach
    public void init() {

        shelter = new Shelter();
        shelter.setId(1L);
        shelter.setShelterType("cats");

        pet = new Pet("Тигра", 5, "хороший и лаcковый кот", null, shelter);
        pet.setId(0L);

    }


    @Test
    void createPet_success() throws Exception {

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(pet);

        //Preparing the expected result

        when(petService.createPet(pet)).thenReturn(pet);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/pet")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateShelter_success() throws Exception{

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(pet);

        //Preparing the expected result

        when(petService.updatePet(pet)).thenReturn(Optional.ofNullable(pet));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/pet")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllPets_success() throws Exception{

        //Input data preparation

        Long shelterId = 0L;

        List<Pet> petsList = new ArrayList<>();
        petsList.add(pet);

        //Preparing the expected result

        when(petService.getAllByShelterId(shelterId)).thenReturn(petsList);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pet?id=" + shelterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getPetById_success() throws Exception {

        //Input data preparation

        Long idPet = pet.getId();

        //Preparing the expected result

        when(petService.getPetById(idPet)).thenReturn(Optional.ofNullable(pet));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pet/" + idPet)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void deletePet_success() throws Exception {

        //Input data preparation

        Long idPet = pet.getId();

        //Preparing the expected result

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/pet/" + idPet)
                        .content(String.valueOf(anyLong())))
                .andExpect(status().isOk());

    }

}