package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ContactsForDogsShelterService.class})
@ExtendWith(SpringExtension.class)
public class ContactsForDogsShelterServiceTest {

    @MockBean
    private ContactsForDogsShelterRepository contactsForDogsShelterRepository;

    @MockBean
    private UserService userService;

    private ContactsForDogsShelterService contactsForDogsShelterService;

    private Long userId = 333L;
    private Long chatId = 222L;
    private String name = "Sam";
    private String userContact = "33333";
    private final ContactsForDogsShelter contact = new ContactsForDogsShelter(userId, name, userContact);

    @BeforeEach
    public void initEach() {
        contactsForDogsShelterService = new ContactsForDogsShelterService(contactsForDogsShelterRepository);
    }

    @Test
    @Disabled
    void save() {
        when(userService.getUserIdByChatId(chatId)).thenReturn(userId);
        when(userService.getUserNameByChatId(chatId)).thenReturn(name);

        //contactsForDogsShelterService.save(chatId, userContact);

        verify(contactsForDogsShelterRepository).save(contact);
    }

    @Test
    void getAll() {
        List<ContactsForDogsShelter> contactsForDogsShelters = new ArrayList<>();
        contactsForDogsShelters.add(contact);

        when(contactsForDogsShelterRepository.findAllContacts()).thenReturn(contactsForDogsShelters);

        List<ContactsForDogsShelter> actual = contactsForDogsShelterService.getAll();
        assertEquals(contactsForDogsShelters, actual);
    }

    @Test
    void deleteAll() {
        List<ContactsForDogsShelter> contactsForDogsShelters = new ArrayList<>();
        contactsForDogsShelters.add(contact);

        contactsForDogsShelterService.deleteAll(contactsForDogsShelters);

        verify(contactsForDogsShelterRepository).deleteAll(contactsForDogsShelters);
    }
}
