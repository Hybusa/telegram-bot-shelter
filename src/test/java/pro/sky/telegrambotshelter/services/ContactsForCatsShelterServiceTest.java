package pro.sky.telegrambotshelter.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.model.User;
import pro.sky.telegrambotshelter.repository.ContactsForCatsShelterRepository;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;

@ContextConfiguration(classes = {ContactsForCatsShelterService.class})
@ExtendWith(SpringExtension.class)
class ContactsForCatsShelterServiceTest {
    @MockBean
    private ContactsForCatsShelterRepository contactsForCatsShelterRepository;

    @Autowired
    private ContactsForCatsShelterService contactsForCatsShelterService;

    /**
     * Method under test: {@link ContactsForCatsShelterService#save(User)}
     */
    @Test
    void testSave_CorrectParams_VerifyMethodCall() {
        ContactsForCatsShelter contactsForCatsShelter = new ContactsForCatsShelter();
        contactsForCatsShelter.setContact("Contact");
        contactsForCatsShelter.setName("Name");
        when(contactsForCatsShelterRepository.save(Mockito.any()))
                .thenReturn(contactsForCatsShelter);

        User user = new User();
        user.setChatId(1L);
        user.setContact("Contact");
        user.setFailed(true);
        user.setId(1L);
        user.setName("Name");
        user.setShelterTypeChoice("Shelter Type Choice");
        contactsForCatsShelterService.save(user);
        verify(contactsForCatsShelterRepository).save(Mockito.any());
    }

    /**
     * Method under test: {@link ContactsForCatsShelterService#getAll()}
     */
    @Test
    void testGetAll_CorrectParams_VerifyMethodCall() {
        ArrayList<ContactsForCatsShelter> contactsForCatsShelterList = new ArrayList<>();
        when(contactsForCatsShelterRepository.findAllContacts()).thenReturn(contactsForCatsShelterList);
        List<ContactsForCatsShelter> actualAll = contactsForCatsShelterService.getAll();
        assertSame(contactsForCatsShelterList, actualAll);
        assertTrue(actualAll.isEmpty());
        verify(contactsForCatsShelterRepository).findAllContacts();
    }

    /**
     * Method under test: {@link ContactsForCatsShelterService#deleteAll(List)}
     */
    @Test
    void testDeleteAll_CorrectParams_VerifyMethodCall() {
        doNothing().when(contactsForCatsShelterRepository).deleteAll(Mockito.<Iterable<ContactsForCatsShelter>>any());
        contactsForCatsShelterService.deleteAll(new ArrayList<>());
        verify(contactsForCatsShelterRepository).deleteAll(Mockito.<Iterable<ContactsForCatsShelter>>any());
    }

    /**
     * Method under test: {@link ContactsForCatsShelterService#deleteByContact(String)}
     */
    @Test
    void testDeleteByContact_CorrectParams_VerifyMethodCall() {
        ContactsForCatsShelter contactsForCatsShelter = mock(ContactsForCatsShelter.class);
        when(contactsForCatsShelter.getUser_Id()).thenReturn(1L);
        doNothing().when(contactsForCatsShelter).setContact(Mockito.anyString());
        doNothing().when(contactsForCatsShelter).setName(Mockito.anyString());
        contactsForCatsShelter.setContact("Contact");
        contactsForCatsShelter.setName("Name");
        Optional<ContactsForCatsShelter> ofResult = Optional.of(contactsForCatsShelter);
        doNothing().when(contactsForCatsShelterRepository).deleteById(anyLong());
        when(contactsForCatsShelterRepository.findByContact(anyString())).thenReturn(ofResult);
        contactsForCatsShelterService.deleteByContact("Contact");
        verify(contactsForCatsShelterRepository).findByContact(anyString());
        verify(contactsForCatsShelterRepository).deleteById(anyLong());
        verify(contactsForCatsShelter).getUser_Id();
        verify(contactsForCatsShelter).setContact(anyString());
        verify(contactsForCatsShelter).setName(anyString());
    }

    /**
     * Method under test: {@link ContactsForCatsShelterService#deleteByContact(String)}
     */
    @Test
    void testDeleteByContact_ContactIdNotFound_DoNothing_MethodCallsVerified() {
        doNothing().when(contactsForCatsShelterRepository).deleteById(anyLong());
        when(contactsForCatsShelterRepository.findByContact(anyString())).thenReturn(Optional.empty());
        contactsForCatsShelterService.deleteByContact("Contact");
        verify(contactsForCatsShelterRepository).findByContact(anyString());
    }
}

