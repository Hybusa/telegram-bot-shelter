package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.UserRepository;
import pro.sky.telegrambotshelter.service.UserService;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    private User user = new User("Sam", 2344L);

    @Test
    void save() {
        UserService userService1 = new UserService(userRepository);

        userService1.save(user);

        verify(userRepository).save(user);
    }

    @Test
    void updateShelterChoiceByChatId() {
        String shelterChoice = "cats";
        UserService userService1 = new UserService(userRepository);

        userService1.updateShelterChoiceByChatId(user, shelterChoice);

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);

    }

    @Test
    void saveContacts() {
        String contact = "2222222";
        UserService userService1 = new UserService(userRepository);

        userService1.saveContacts(user, contact);

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);
    }

    @Test
    void getMapUsersChatIdWithChoice() {
        Map<Long, String> expected = new HashMap<>();
        List<User> userList = new ArrayList<>();
        user.setShelterTypeChoice("cats");
        userList.add(user);
        expected.put(user.getChatId(), user.getShelterTypeChoice());
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findAll()).thenReturn(userList);

        Map<Long, String> actual = userService1.getMapUsersChatIdWithChoice();
        assertEquals(expected, actual);
    }

    @Test
    void getUserIdByChatId() {
        user.setId(222L);
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        Long actual = userService1.getUserIdByChatId(user.getChatId());
        assertEquals(user.getId(), actual);
    }

    @Test
    void getUserNameByChatId() {
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService1.getUserNameByChatId(user.getChatId());
        assertEquals(user.getName(), actual);
    }

    @Test
    void getUserById() {
        Optional<User> user = Optional.of(new User("Sam", 2344L));
        user.get().setId(222l);
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findById(user.get().getId())).thenReturn(user);

        Optional<User> actual = userService1.getUserById(user.get().getId());
        assertEquals(user, actual);
    }

    @Test
    void getUsersShelterTypeChoice() {
        user.setShelterTypeChoice("cats");
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService1.getUsersShelterTypeChoice(user.getChatId());
        assertEquals(user.getShelterTypeChoice(), actual);
    }

    @Test
    void getContact() {
        user.setContact("2222222");
        UserService userService1 = new UserService(userRepository);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService1.getContact(user.getChatId());
        assertEquals(user.getContact(), actual);
    }
}
