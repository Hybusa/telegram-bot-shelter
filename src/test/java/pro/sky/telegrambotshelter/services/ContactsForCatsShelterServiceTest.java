package pro.sky.telegrambotshelter.services;

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

    private final ContactsForCatsShelter contact = new ContactsForCatsShelter(333L, "Sam", "33333");

    @Test
    void save() {
        ContactsForCatsShelterService contactsForCatsShelterService = new ContactsForCatsShelterService(userService, contactsForCatsShelterRepository);

        when(userService.getUserIdByChatId(3333L)).thenReturn(333L);
        when(userService.getUserNameByChatId(3333L)).thenReturn("Sam");

        contactsForCatsShelterService.save(3333L, "33333");

        verify(contactsForCatsShelterRepository).save(contact);
    }

    @Test
    void getAll() {
        ContactsForCatsShelterService contactsForCatsShelterService = new ContactsForCatsShelterService(userService, contactsForCatsShelterRepository);
        List<ContactsForCatsShelter> contactsForCatsShelters = new ArrayList<>();
        contactsForCatsShelters.add(contact);

        when(contactsForCatsShelterRepository.findAllContacts()).thenReturn(contactsForCatsShelters);

        List<ContactsForCatsShelter> actual = contactsForCatsShelterService.getAll();
        assertEquals(contactsForCatsShelters, actual);
    }

    @Test
    void deleteAll(){
        ContactsForCatsShelterService contactsForCatsShelterService = new ContactsForCatsShelterService(userService, contactsForCatsShelterRepository);
        List<ContactsForCatsShelter> contactsForCatsShelters = new ArrayList<>();
        contactsForCatsShelters.add(contact);

        contactsForCatsShelterService.deleteAll(contactsForCatsShelters);

        verify(contactsForCatsShelterRepository).deleteAll(contactsForCatsShelters);
    }
}
