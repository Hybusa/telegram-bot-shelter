package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ContactsForCatsShelterService.class})
@ExtendWith(SpringExtension.class)
public class ContactsForCatsShelterServiceTest {

    @MockBean
    private ContactsForCatsShelterRepository contactsForCatsShelterRepository;

    @MockBean
    private UserService userService;

    private Long userId = 333L;
    private Long chatId = 222L;
    private String name = "Sam";
    private String userContact = "33333";
    private final ContactsForCatsShelter contact = new ContactsForCatsShelter(userId, name, userContact);
    private ContactsForCatsShelterService contactsForCatsShelterService;
    @BeforeEach
    public void initEach() {
        contactsForCatsShelterService = new ContactsForCatsShelterService(userService, contactsForCatsShelterRepository);
    }

    @Test
    void save() {
        when(userService.getUserIdByChatId(chatId)).thenReturn(userId);
        when(userService.getUserNameByChatId(chatId)).thenReturn(name);

        contactsForCatsShelterService.save(chatId, userContact);

        verify(contactsForCatsShelterRepository).save(contact);
    }

    @Test
    void getAll() {
        List<ContactsForCatsShelter> contactsForCatsShelters = new ArrayList<>();
        contactsForCatsShelters.add(contact);

        when(contactsForCatsShelterRepository.findAllContacts()).thenReturn(contactsForCatsShelters);

        List<ContactsForCatsShelter> actual = contactsForCatsShelterService.getAll();
        assertEquals(contactsForCatsShelters, actual);
    }

    @Test
    void deleteAll(){
        List<ContactsForCatsShelter> contactsForCatsShelters = new ArrayList<>();
        contactsForCatsShelters.add(contact);

        contactsForCatsShelterService.deleteAll(contactsForCatsShelters);

        verify(contactsForCatsShelterRepository).deleteAll(contactsForCatsShelters);
    }
}
