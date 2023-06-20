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
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    private final Long idUser = 1L;
    private final String nameUser = "Виталик";
    private final String shelterChoice = "cats";

    @BeforeEach
    public void init() {

        user = new User();
        user.setId(idUser);
        user.setName(nameUser);
        user.setShelterTypeChoice(shelterChoice);

    }

    @Test
    void createUser_success() throws Exception {

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(user);

        //Preparing the expected result

        when(userService.createUser(user)).thenReturn(user);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateUser_success() throws Exception {

        //Input data preparation

        String json = new ObjectMapper().writeValueAsString(user);

        //Preparing the expected result

        when(userService.updateUser(user)).thenReturn(Optional.ofNullable(user));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/user")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getAllUserByShelterChoice_success() throws Exception {

        //Input data preparation

        List<User> userList = new ArrayList<>();
        userList.add(user);

        //Preparing the expected result

        when(userService.getAllUsersByShelterTypeChoice(shelterChoice)).thenReturn(userList);

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user?shelterChoice=" + shelterChoice)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getUserById_success() throws Exception {

        //Input data preparation

        //Preparing the expected result

        when(userService.getUserById(idUser)).thenReturn(Optional.ofNullable(user));

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/" + idUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void deleteSUser_success() throws Exception {

        //Input data preparation

        //Preparing the expected result

        //Test start

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/user/" + idUser)
                        .accept(String.valueOf(anyLong())))
                .andExpect(status().isOk());

    }

}