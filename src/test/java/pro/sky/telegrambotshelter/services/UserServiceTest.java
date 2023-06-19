package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.UserRepository;
import pro.sky.telegrambotshelter.service.UserService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;

    private String shelterChoice = "cats";
    private Long userId = 222L;
    private String contact = "2222222";
    private String name = "Sam";
    private Long chatId = 2344L;
    private User user = new User(name, chatId);

    @Autowired
    private UserService userService;

//    @BeforeEach
//    public void initEach() {
//        userService = new UserService(userRepository);
//    }

    @Test
    void save() {
        userService.save(user);

        verify(userRepository).save(user);
    }

    @Test
    void updateShelterChoiceByChatId() {
        userService.updateShelterChoiceByChatId(user, shelterChoice);

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);

    }

    @Test
    void saveContacts() {
        userService.saveContacts(user, contact);

        verify(userRepository).findUserByChatId(user.getChatId());
        verify(userRepository).save(user);
    }

    @Test
    void getMapUsersChatIdWithChoice() {
        Map<Long, String> expected = new HashMap<>();
        List<User> userList = new ArrayList<>();
        userList.add(user);
        user.setShelterTypeChoice(shelterChoice);
        expected.put(user.getChatId(), user.getShelterTypeChoice());

        when(userRepository.findAll()).thenReturn(userList);

        Map<Long, String> actual = userService.getMapUsersChatIdWithChoice();
        assertEquals(expected, actual);
    }

    @Test
    void getUserIdByChatId() {
        user.setId(userId);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

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
    void getUserById() {
        Optional<User> user = Optional.of(new User(name, chatId));
        user.get().setId(userId);

        when(userRepository.findById(user.get().getId())).thenReturn(user);

        Optional<User> actual = userService.getUserById(user.get().getId());
        assertEquals(user, actual);
    }

    @Test
    void getUsersShelterTypeChoice() {
        user.setShelterTypeChoice(shelterChoice);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService.getUsersShelterTypeChoice(user.getChatId());
        assertEquals(user.getShelterTypeChoice(), actual);
    }

    @Test
    void getContact() {
        user.setContact(contact);

        when(userRepository.findUserByChatId(user.getChatId())).thenReturn(Optional.of(user));

        String actual = userService.getContact(user.getChatId());
        assertEquals(user.getContact(), actual);
    }
}
