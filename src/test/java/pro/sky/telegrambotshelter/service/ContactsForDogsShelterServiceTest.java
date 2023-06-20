package pro.sky.telegrambotshelter.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForDogsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.ContactsForDogsShelterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ContactsForDogsShelterService.class})
@ExtendWith(SpringExtension.class)
class ContactsForDogsShelterServiceTest {
    @MockBean
    private ContactsForDogsShelterRepository contactsForDogsShelterRepository;

    @Autowired
    private ContactsForDogsShelterService contactsForDogsShelterService;

    /**
     * Method under test: {@link ContactsForDogsShelterService#save(User)}
     */
    @Test
    void testSave_CorrectParams_VerifyMethodCall() {
        ContactsForDogsShelter contactsForDogsShelter = new ContactsForDogsShelter();
        contactsForDogsShelter.setContact("Contact");
        contactsForDogsShelter.setName("Name");
        when(contactsForDogsShelterRepository.save(Mockito.any()))
                .thenReturn(contactsForDogsShelter);

        User user = new User();
        user.setChatId(1L);
        user.setContact("Contact");
        user.setFailed(true);
        user.setId(1L);
        user.setName("Name");
        user.setShelterTypeChoice("Shelter Type Choice");
        contactsForDogsShelterService.save(user);
        verify(contactsForDogsShelterRepository).save(Mockito.any());
    }

    /**
     * Method under test: {@link ContactsForDogsShelterService#getAll()}
     */
    @Test
    void testGetAll_CorrectParams_VerifyMethodCall() {
        ArrayList<ContactsForDogsShelter> contactsForDogsShelterList = new ArrayList<>();
        when(contactsForDogsShelterRepository.findAllContacts()).thenReturn(contactsForDogsShelterList);
        List<ContactsForDogsShelter> actualAll = contactsForDogsShelterService.getAll();
        assertSame(contactsForDogsShelterList, actualAll);
        assertTrue(actualAll.isEmpty());
        verify(contactsForDogsShelterRepository).findAllContacts();
    }

    /**
     * Method under test: {@link ContactsForDogsShelterService#deleteAll(List)}
     */
    @Test
    void testDeleteAll_CorrectParams_VerifyMethodCall() {
        doNothing().when(contactsForDogsShelterRepository).deleteAll(Mockito.<Iterable<ContactsForDogsShelter>>any());
        contactsForDogsShelterService.deleteAll(new ArrayList<>());
        verify(contactsForDogsShelterRepository).deleteAll(Mockito.<Iterable<ContactsForDogsShelter>>any());
    }

    /**
     * Method under test: {@link ContactsForDogsShelterService#deleteByContact(String)}
     */
    @Test
    void testDeleteByContact_CorrectParams_VerifyMethodCall() {
        ContactsForDogsShelter contactsForDogsShelter = mock(ContactsForDogsShelter.class);
        when(contactsForDogsShelter.getUser_Id()).thenReturn(1L);
        doNothing().when(contactsForDogsShelter).setContact(Mockito.anyString());
        doNothing().when(contactsForDogsShelter).setName(Mockito.anyString());
        contactsForDogsShelter.setContact("Contact");
        contactsForDogsShelter.setName("Name");
        Optional<ContactsForDogsShelter> ofResult = Optional.of(contactsForDogsShelter);
        doNothing().when(contactsForDogsShelterRepository).deleteById(anyLong());
        when(contactsForDogsShelterRepository.findByContact(anyString())).thenReturn(ofResult);
        contactsForDogsShelterService.deleteByContact("Contact");
        verify(contactsForDogsShelterRepository).findByContact(anyString());
        verify(contactsForDogsShelterRepository).deleteById(anyLong());
        verify(contactsForDogsShelter).getUser_Id();
        verify(contactsForDogsShelter).setContact(anyString());
        verify(contactsForDogsShelter).setName(anyString());
    }

    /**
     * Method under test: {@link ContactsForDogsShelterService#deleteByContact(String)}
     */
    @Test
    void testDeleteByContact_ContactIdNotFound_DoNothing_MethodCallsVerified() {
        doNothing().when(contactsForDogsShelterRepository).deleteById(anyLong());
        when(contactsForDogsShelterRepository.findByContact(anyString())).thenReturn(Optional.empty());
        contactsForDogsShelterService.deleteByContact("Contact");
        verify(contactsForDogsShelterRepository).findByContact(anyString());
    }
}

