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
import pro.sky.telegrambotshelter.model.Shelter;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShelterController.class)
class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelterService shelterService;

    private Shelter shelter;

    private final Long idShelter = 0L;
    private final String shelterType = "cats";

    @BeforeEach
    public void init() {

        shelter = new Shelter();
        shelter.setId(idShelter);
        shelter.setShelterType(shelterType);

    }

    @Test
    void createShelter_success() throws Exception {

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(shelter);

        //Preparing the expected result

        when(shelterService.createShelter(shelter)).thenReturn(shelter);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/shelter")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateShelter_success() throws Exception {

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(shelter);

        //Preparing the expected result

        when(shelterService.updateShelter(shelter)).thenReturn(Optional.ofNullable(shelter));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/shelter")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    void getAllShelters_success() throws Exception {

        //Input data preparation

        List<Shelter> shelterList = new ArrayList<>();
        shelterList.add(shelter);

        //Preparing the expected result

        when(shelterService.getAllByShelterType(shelterType)).thenReturn(shelterList);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/shelter?shelterType=" + shelterType)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getShelterById_success() throws Exception {

        //Input data preparation

        //Preparing the expected result

        when(shelterService.getShelterById(idShelter)).thenReturn(Optional.ofNullable(shelter));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/shelter/" + idShelter)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void deleteShelter_success() throws Exception {

        //Input data preparation

        //Preparing the expected result

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/shelter/" + idShelter)
                        .content(String.valueOf(anyLong())))
                .andExpect(status().isOk());

    }

}