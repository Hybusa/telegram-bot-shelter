package pro.sky.telegrambotshelter.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.telegrambotshelter.model.ContactsForCatsShelter;
import pro.sky.telegrambotshelter.service.ContactsForCatsShelterService;
import pro.sky.telegrambotshelter.service.ContactsForDogsShelterService;
import pro.sky.telegrambotshelter.service.ShelterService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ContactScheduler.class})
@ExtendWith(SpringExtension.class)
public class ContactSchedulerTest {

    @Autowired
    private ContactScheduler contactScheduler;
    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private ShelterService shelterService;
    @MockBean
    private ContactsForCatsShelterService contactsForCatsShelterService;
    @MockBean
    private ContactsForDogsShelterService contactsForDogsShelterService;

    /* @Test
    void checkerForCatShelter() {
        List<ContactsForCatsShelter> contactsForCatsShelters = new ArrayList<>();
        ContactsForCatsShelter contacts = new ContactsForCatsShelter(333L, "Sam", "3333");
        contactsForCatsShelters.add(contacts);

        when(shelterService.getVolunteerChatId("cats")).thenReturn(333);
        when(contactsForCatsShelterService.getAll()).thenReturn(contactsForCatsShelters);
        SendResponse sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(telegramBot.execute(any(SendMessage.class))).thenReturn(sendResponse);

        verify(telegramBot).execute(Mockito.any(SendMessage.class));
        verify(sendResponse).isOk();
        verify(contactsForCatsShelterService).deleteAll(contactsForCatsShelters);
    }*/

}

