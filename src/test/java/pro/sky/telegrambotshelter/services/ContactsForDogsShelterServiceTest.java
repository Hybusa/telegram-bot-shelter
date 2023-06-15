package pro.sky.telegrambotshelter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
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

    private final ContactsForDogsShelter contact = new ContactsForDogsShelter(333L, "Sam", "33333");


    @Test
    void save() {
        ContactsForDogsShelterService contactsForDogsShelterService = new ContactsForDogsShelterService(userService, contactsForDogsShelterRepository);

        when(userService.getUserIdByChatId(3333L)).thenReturn(333L);
        when(userService.getUserNameByChatId(3333L)).thenReturn("Sam");

        contactsForDogsShelterService.save(3333L, "33333");

        verify(contactsForDogsShelterRepository).save(contact);
    }

    @Test
    void getAll() {
        ContactsForDogsShelterService contactsForDogsShelterService = new ContactsForDogsShelterService(userService, contactsForDogsShelterRepository);
        List<ContactsForDogsShelter> contactsForDogsShelters = new ArrayList<>();
        contactsForDogsShelters.add(contact);

        when(contactsForDogsShelterRepository.findAllContacts()).thenReturn(contactsForDogsShelters);

        List<ContactsForDogsShelter> actual = contactsForDogsShelterService.getAll();
        assertEquals(contactsForDogsShelters, actual);
    }

    @Test
    void deleteAll(){
        ContactsForDogsShelterService contactsForDogsShelterService = new ContactsForDogsShelterService(userService, contactsForDogsShelterRepository);
        List<ContactsForDogsShelter> contactsForDogsShelters = new ArrayList<>();
        contactsForDogsShelters.add(contact);

        contactsForDogsShelterService.deleteAll(contactsForDogsShelters);

        verify(contactsForDogsShelterRepository).deleteAll(contactsForDogsShelters);
    }
}
