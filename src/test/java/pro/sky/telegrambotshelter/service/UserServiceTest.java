package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    private User user;
    @Autowired
    private UserService userService;


    @BeforeEach
    public void initEach() {
        user = mock(User.class);
    }

    @Test
    void save() {
        userService.save(user);

        verify(userRepository).save(user);
    }

    @Test
    void updateShelterChoiceByChatId() {
        String shelterType = "cat";
        userService.updateShelterChoiceByChatId(user, shelterType);

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);

    }

    @Test
    void saveContacts() {
        userService.saveContacts(user, user.getContact());

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);
    }

    @Test
    void createUser() {
        when(userRepository.save(user)).thenReturn(user);

        User actual = userService.createUser(user);
        assertEquals(user, actual);
    }

    @Test
    void updateUser() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        Optional<User> actual = userService.updateUser(user);
        assertEquals(Optional.of(user), actual);
    }

    @Test
    void updateUserWithOptionalEmpty() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        Optional<User> actual = userService.updateUser(user);
        assertEquals(Optional.empty(), actual);
    }

    @Test
    void getAllUsers() {
        List<User> expected = new ArrayList<>();
        expected.add(user);
        when(userRepository.findAll()).thenReturn(expected);

        List<User> actual = userService.getAllUsers();
        assertEquals(expected, actual);
    }

    @Test
    void getAllUsersByShelterTypeChoice() {
        String shelterType = "cats";
        List<User> expected = new ArrayList<>();
        expected.add(user);
        when(userRepository.findAllByShelterTypeChoice(shelterType)).thenReturn(expected);

        List<User> actual = userService.getAllUsersByShelterTypeChoice(shelterType);
        assertEquals(expected, actual);
    }

    @Test
    void deleteUserById() {
        when(userRepository.existsById(user.getId())).thenReturn(true);

        userService.deleteUserById(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void deleteUserByIdWithNotFoundException() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        String expectedMessage = "Pet id not found";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUserById(user.getId())
        );


        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getMapUsersChatIdWithChoice() {
        Map<Long, String> expected = new HashMap<>();
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String type = "test";
        Long chatId = 22L;
        expected.put(chatId, type);

        when(user.getChatId()).thenReturn(chatId);
        when(user.getShelterTypeChoice()).thenReturn(type);
        when(userRepository.findAll()).thenReturn(userList);

        Map<Long, String> actual = userService.getMapUsersChatIdWithChoice();
        assertEquals(expected, actual);
    }

    @Test
    void getUserIdByChatId() {
        Long id = 33L;

        when(user.getId()).thenReturn(id);
        when(userRepository.findUserByChatId(anyLong())).thenReturn(Optional.of(user));

        Long actual = userService.getUserIdByChatId(user.getChatId());
        assertEquals(user.getId(), actual);
    }

    @Test
    void getUserNameByChatId() {
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService.getUserNameByChatId(user.getChatId());
        assertEquals(user.getName(), actual);
    }

    @Test
    void getUserNameByChatIdWithNotFoundException() {
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.empty());
        String expectedMessage = "User was not found by chatId";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserNameByChatId(user.getId())
        );


        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> actual = userService.getUserById(user.getId());
        assertEquals(Optional.of(user), actual);
    }

    @Test
    void getListUsersContactsWithDodShelter() {
        List<String> expected = new ArrayList<>();
        List<Long> id = new ArrayList<>();


        when(userRepository.listUsersIdFromDogsShelter()).thenReturn(id);

        List<String> actual = userService.getListUsersContactsWithDodShelter();
        assertEquals(expected, actual);
    }

    @Test
    void getUsersShelterTypeChoice() {
        when(userRepository.findUserByChatId(anyLong())).thenReturn(Optional.of(user));

        String actual = userService.getUsersShelterTypeChoice(user.getChatId());
        assertEquals(user.getShelterTypeChoice(), actual);
    }

    @Test
    void getUsersShelterTypeChoiceWithNotFoundException() {
        when(userRepository.findUserByChatId(anyLong())).thenReturn(Optional.empty());
        String expectedMessage = "User was not found by chatId";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUsersShelterTypeChoice(user.getChatId())
        );


        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteUsersByChatId() {
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        userService.deleteUsersByChatId(user.getChatId());

        verify(userRepository).delete(user);
    }

    @Test
    void getContact() {
        when(user.getContact()).thenReturn("contact");
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService.getContact(user.getChatId());
        assertEquals(user.getContact(), actual);
    }

    @Test
    void getContactWithNotFoundException() {
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.empty());
        String expectedMessage = "User was not found by chatId";

        Exception exception = assertThrows(
                NotFoundException.class,
                () -> userService.getContact(user.getChatId())
        );


        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getUserByChatId() {
        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        Optional<User> actual = userService.getUserByChatId(user.getChatId());
        assertEquals(Optional.of(user), actual);
    }
}